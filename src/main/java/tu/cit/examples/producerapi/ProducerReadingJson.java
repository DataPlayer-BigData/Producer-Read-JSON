package tu.cit.examples.producerapi;

//import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class ProducerReadingJson {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "my-partition-examples");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ObjectMapper mapper = new ObjectMapper();
        //CASE 1
        String json ="{\"productid\":10001,\"productName\":\"laptop\",\"brand\":\"HP\",\"price\":3000}";

        KafkaProducer<Integer,ProductSchema> producer = new KafkaProducer<Integer, ProductSchema>(props);

        try{
            ProductSchema productObject = mapper.readValue(json,ProductSchema.class);
//            System.out.println("Product ID : " + productSchema.getProductid());
//            System.out.println("Product Name : " + productSchema.getProductName());
//            System.out.println("Brand : " + productSchema.getBrand());
//            System.out.println("Price : " + productSchema.getPrice());
            logger.info("Producer is created....");
            ProducerRecord<Integer,ProductSchema> record = new ProducerRecord<Integer, ProductSchema>("producer-read-json",productObject.getProductid(),productObject);
            producer.send(record,new Callback(){
                public void onCompletion(RecordMetadata metadata, Exception e){
                    if (e != null)
                        System.out.println(e.getMessage());
                    else
                        System.out.println(metadata.topic() + " : " + metadata.partition() + " : " + metadata.offset());
                }
            });
        }catch(IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        producer.close();
    }
}
