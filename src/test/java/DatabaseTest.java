/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imos.pi.database.DatabaseList;
import com.imos.pi.database.TimeTempHumidData;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Pintu
 */
public class DatabaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode array;
    @BeforeClass
    public static void setUp() throws IOException {
        // = MAPPER.readTree(IOUtils.readFileString("27_8_2016_0_0_0.json"));
//        array = MAPPER.readValue(DatabaseTest.class.getClassLoader().getResourceAsStream("sample.json"), JsonNode.class);
//        array = MAPPER.readValue(DatabaseTest.class.getClassLoader().getResourceAsStream("27_8_2016_0_0_0.json"), JsonNode.class);
        array = MAPPER.readValue(DatabaseTest.class.getClassLoader().getResourceAsStream("28_8_2016.json"), JsonNode.class);
        Iterator<JsonNode> itr = array.iterator();
        while (itr.hasNext()) {
            DatabaseList.getInstance().addData(MAPPER.readValue(MAPPER.writeValueAsString(itr.next()), TimeTempHumidData.class));
        }
    }
    
    @AfterClass
    public static void tearDown() throws IOException {
        //MAPPER.writeValue(new FileOutputStream("28_8_2016.json"), DatabaseList.getInstance().getAllData());
    }

    @Test
    public void testSize() {
        System.out.println(DatabaseList.getInstance().size());
    }

    @Test
    public void testDayData() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 26);
        System.out.println(DatabaseList.getInstance().getDayData(cal.getTimeInMillis()).size());
    }

    @Test
    public void testDayDatabase() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 27);
        System.out.println(cal.getTimeInMillis());
        cal.setTimeInMillis(cal.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println(cal.getTimeInMillis());
        
        System.out.println(DatabaseList.DAY_LIST_INDEX_MAP.size());
        new TreeMap<>(DatabaseList.DAY_LIST_INDEX_MAP).forEach((k, v) -> {
            System.out.println(new Date(k) + " : " + v);
        });
    }
    
    @Test
    public void jsonTest() {
        TimeTempHumidData dat = new TimeTempHumidData();
        System.out.println(dat);
    }
}
