package ru.tsystems.karpova.connector;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import ru.tsystems.karpova.requests.AddStationRequestInfo;
import ru.tsystems.karpova.respond.AddStationRespondInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {

    @Mock
    Socket stub;
    RequestHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new RequestHandler(stub);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddRoute() throws Exception {

    }

    @Test
    public void testGetAllWays() throws Exception {

    }

    @Test
    public void testViewPassengerByTrain() throws Exception {

    }

    @Test
    public void testGetAllTrains() throws Exception {

    }

    @Test
    public void testAddStation() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(stream);

        handler.addStation(outputStream, new AddStationRequestInfo("test_station"+System.currentTimeMillis()));

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        Object retVal = objectInputStream.readObject();

        Assert.assertTrue(retVal instanceof AddStationRespondInfo);
        Assert.assertEquals(((AddStationRespondInfo)retVal).getStatus(),AddStationRespondInfo.OK_STATUS);
    }

    @Test
    public void testAddTrain() throws Exception {

    }

    @Test
    public void testBuyTicket() throws Exception {

    }

    @Test
    public void testStationFromBeforeStationToInList() throws Exception {

    }

    @Test
    public void testStationInList() throws Exception {

    }

    @Test
    public void testCalcFreeSeats() throws Exception {

    }

    @Test
    public void testScheduleByStation() throws Exception {

    }

    @Test
    public void testFindTrain() throws Exception {

    }

    @Test
    public void testGetAllStation() throws Exception {

    }

    @Test
    public void testGetAllRoutes() throws Exception {

    }
}
