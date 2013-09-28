package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Train;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrainDAO {
    private static Logger log = Logger.getLogger(TrainDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List findTrain(String stationFrom, String stationTo, Date dateFrom, Date dateTo) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("select t.name,\n" +
                "(t.departure + SUM(case when wayTime.id = wayA.id then 0 else wayTime.time end)) as startTime\n" +
                "from Train t \n" +
                "join t.routeByIdRoute r\n" +
                "join r.schedulesById scheduleA\n" +
                "join scheduleA.wayByIdWay wayA\n" +
                "join wayA.stationByIdStation1 stationA\n" +
                "join r.schedulesById scheduleB\n" +
                "join scheduleB.wayByIdWay wayB\n" +
                "join wayB.stationByIdStation2 stationB\n" +
                "join r.schedulesById scheduleTime\n" +
                "join scheduleTime.wayByIdWay wayTime\n" +
                "where stationA.name = ?\n" +
                "and stationB.name = ?\n" +
                "and scheduleA.seqNumber <= scheduleB.seqNumber\n" +
                "and scheduleTime.seqNumber <= scheduleA.seqNumber\n" +
                "group by t.name\n")// +
//                "having col_1_0_ >= ? and col_1_0_ <= ?")
                .setParameter(1, stationFrom)
                .setParameter(2, stationTo)
//                .setParameter(3, dateFrom)
//                .setParameter(4, dateTo)
                .getResultList();
        return results;

    }

    public static List<Object[]> findTrainByStation(String station) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("select t.name,\n" +
                "(t.departure + SUM(case when wayTime.id = wayA.id then 0 else wayTime.time end)) as startTime\n" +
                "from Train t \n" +
                "join t.routeByIdRoute r\n" +
                "join r.schedulesById scheduleA\n" +
                "join scheduleA.wayByIdWay wayA\n" +
                "join wayA.stationByIdStation1 stationA\n" +
                "join r.schedulesById scheduleTime\n" +
                "join scheduleTime.wayByIdWay wayTime\n" +
                "where stationA.name = ?\n" +
                "and scheduleTime.seqNumber <= scheduleA.seqNumber\n" +
                "group by t.name\n")
                .setParameter(1, station)
                .getResultList();
        return results;
    }

    public static int freeSeatsOnTrain(String train) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("select t.totalSeats - count(ticket.id)\n" +
                "from Train t \n" +
                "join t.ticketsById ticket\n" +
                "where t.name = ?\n" +
                "group by t.name")
                .setParameter(1, train)
                .getResultList();
        return 1;//results.size() == 1 ? results.get(0) : -1;
    }

//    public static void main(String[] args) throws ParseException {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        List trains = findTrain("Nvk", "SPb", dateFormat.parse("2013-07-19") , dateFormat.parse("2013-10-19"));
//        System.out.println(trains.size());
//    }
}