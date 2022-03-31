import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.ShadowStateFactory;

import java.util.HashMap;


public class ShadowStateTest {

    private Gson shadowGson = null;

    @BeforeEach
    public void TestSetup()
    {
        if (shadowGson == null)
        {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
            gsonBuilder.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());

            ShadowStateFactory shadowStateFactory = new ShadowStateFactory();
            gsonBuilder.registerTypeAdapterFactory(shadowStateFactory);

            shadowGson = gsonBuilder.create();
        }
    }

    @Test
    public void TestHardCoded()
    {
        ShadowState state = new ShadowState();
        state.desired = new HashMap<String, Object>() {{
            put("ThingOne", 10.0);
            put("ThingTwo", "Bob");
        }};
        String state_json = shadowGson.toJson(state);
        String expected_json = "{\"desired\":{\"ThingTwo\":\"Bob\",\"ThingOne\":10.0}}";

        assertEquals(state_json, expected_json);
    }

    @Test
    public void TestCompareJsonOutputs()
    {
        ShadowState state = new ShadowState();
        state.desired = new HashMap<String, Object>() {{
            put("ThingOne", 10.0);
            put("ThingTwo", "Bob");
        }};
        String state_json = shadowGson.toJson(state);

        String state_two_input = "{\"desired\":{\"ThingTwo\":\"Bob\",\"ThingOne\":10.0}}";
        ShadowState state_two = shadowGson.fromJson(state_two_input, ShadowState.class);
        String state_two_json = shadowGson.toJson(state_two);

        assertEquals(state_json, state_two_json);
    }

    @Test
    public void TestNullSendThroughJson()
    {
        ShadowState state = new ShadowState();
        state.desired = new HashMap<String, Object>() {{
            put("ThingOne", 10.0);
            put("ThingTwo", "Bob");
        }};
        state.reported = null;
        state.reportedIsNullable = true;
        String state_json = shadowGson.toJson(state);
        String expected_json = "{\"desired\":{\"ThingTwo\":\"Bob\",\"ThingOne\":10.0},\"reported\":null}";
        assertEquals(state_json, expected_json);
    }
}
