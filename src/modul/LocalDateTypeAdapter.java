package modul;

import com.google.gson.*;

import java.lang.reflect.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class LocalDateTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {//нужен для
    //корректной де/сериализации LDT формата

    private static final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm");

    @Override
    public JsonElement serialize(final LocalDateTime date, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {

        String jsonString = json.getAsString();
        String[] str = jsonString.split("T");
        LocalDate publicationDate = LocalDate.parse(str[0]);
        LocalTime publicationTime = LocalTime.parse(str[1]);

        return LocalDateTime.of(publicationDate,publicationTime);
    }
}
