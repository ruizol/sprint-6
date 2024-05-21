import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructType, ArrayType, TimestampType}
import org.apache.spark.sql.streaming.Trigger // import Trigger


object Streaming {
    def main(args: Array[String]) {

        val conf = new SparkConf()
        .setAppName("SparkStreaming")
        .set("spark.streaming.stopGracefullyOnShutdown", "true")

        val spark = SparkSession.builder()
        .master("local[2]")
        .config(conf)
        .getOrCreate()

        spark.sparkContext.setLogLevel("ERROR") // Pour ne pas avoir des informations inutiles dans le terminal lors de l'exécution. 

       import spark.implicits._

        val schema = new StructType()
            .add("id", IntegerType, true)
            .add("order_time", TimestampType)
            .add("shop", StringType, true)
            .add("name", StringType, true)
            .add("phoneNumber", StringType, true)
            .add("address", StringType, true)
            .add("pizzas", new StructType()
                .add("pizzaName", StringType)
                .add("price", IntegerType)
        )

        // read streaming flow
        val df = spark.readStream
        .schema(schema)
        .option("maxFilesPerTrigger", 2)
        .json("files/")

        // clean dataframe
        val df_clean = df.select(
        $"id" as "id",
        $"order_time" as "order_time",
        $"shop" as "shop",
        $"name" as "name",
        $"address" as "address",
        $"phoneNumber" as "phoneNumber",
        col("pizzas").getItem("pizzaName") as "pizzaName",
        col("pizzas").getItem("price") as "price")
        //val filtre = df_clean.filter($"price" > 10)
        val price_filtre = df_clean.filter(($"price" > 8) && ($"price" < 10) && ($"shop" === "Pizza Hut"))

        

        //val aggregate = df_clean.groupBy("shop").agg(mean("price"))
        val aggregate = df_clean.groupBy("shop").agg(mean("price"), sum("price"), max("price"), min("price"))


        //aggregate.writeStream
        //.outputMode("complete")
        //.format("console")
        //.start()
        //.awaitTermination

        val schema_batch = new StructType()
        .add("pizzas", StringType)
        .add("topping_1", StringType)
        .add("topping_2", StringType)
        .add("topping_3", StringType)


        val toppings_temp = spark.read
        .option("schema_batch","true")
        .json("batch/batch.json")


        val toppings = toppings_temp.select(
            $"pizzas" as "pizzaName",
            $"topping_1" as "topping_1",
            $"topping_2" as "topping_2",
            $"topping_3" as "topping_3"
        )

        //val df_join = df_clean.join(toppings, Seq("pizzaName", "pizzaName"), "left_outer")

        //val df_with_window = df_clean
        //  .groupBy(window($"order_time", "5 seconds", "1 seconds"), $"pizzaName")
        //  .agg(sum($"price").as("sum_price"))

        val df_with_window = df_clean
          .withWatermark("order_time", "1 minute")
          .groupBy(window($"order_time", "5 seconds", "1 seconds"), $"pizzaName")
          .agg(sum($"price").as("sum_price"))

        val df_join = df_with_window.join(toppings, Seq("pizzaName"), "left_outer")
          .select($"window.start" as "start", $"window.end" as "end", $"pizzaName", $"sum_price", $"topping_1", $"topping_2", $"topping_3")

        // display streaming flow
        df_join.writeStream
        .outputMode("update")
        .format("console")
        .trigger(Trigger.ProcessingTime("4 seconds"))
        .start()
        .awaitTermination()
    }
}