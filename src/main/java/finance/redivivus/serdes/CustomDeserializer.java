package finance.redivivus.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomDeserializer<T> implements Deserializer<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> tClass;

    public CustomDeserializer(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(data, tClass);
        } catch (Exception e) {
            //throw new SerializationException("Error when deserializing byte[] to " + tClass.getName(), e);
            return null;
        }
    }

    @Override
    public void close() {
    }
}