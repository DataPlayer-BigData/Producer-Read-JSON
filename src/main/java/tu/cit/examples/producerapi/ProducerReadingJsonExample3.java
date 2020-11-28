package tu.cit.examples.producerapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import tu.cit.examples.producerapi.ProductSchema;
public class ProducerReadingJsonExample3 {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "my-partition-examples");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ObjectMapper mapper = new ObjectMapper();

        KafkaProducer<Integer,ProductSchema> producer = new KafkaProducer<Integer, ProductSchema>(props);

        try {
            // Reading json record from product.json file
            List<ProductSchema> productObject = Arrays.asList(mapper.readValue(Paths.get("data/products.json").toFile(), ProductSchema[].class));


            logger.info("Producer is created....");
            for (int i = 0; i < productObject.size(); i++) {
                ProducerRecord<Integer, ProductSchema> record = new ProducerRecord<Integer, ProductSchema>("producer-read-json", productObject.get(i).getProductid(),
                        productObject.get(i));
                producer.send(record, new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null)
                            System.out.println(e.getMessage());
                        else
                            System.out.println(metadata.topic() + " : " + metadata.partition() + " : " + metadata.offset());
                    }
                });
            }
            }catch(IOException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        producer.close();
    }
}
