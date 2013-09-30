package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Passenger;
import ru.tsystems.karpova.entities.Station;
import ru.tsystems.karpova.entities.Train;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TrainDAO {
    private static Logger log = Logger.getLogger(TrainDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static Train loadTrain(String trainName) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("from Train where name=?").setParameter(1, trainName).getResultList();
        return results == null || results.isEmpty() ? null : (Train) results.get(0);
    }

    public static List findTrain(String stationFrom, String stationTo, Date dateFrom, Date dateTo) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start findTrain select");
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
        log.debug("Start findTrainByStation select");
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

    public static boolean checkDepartureTime(Train train, Station stationFrom) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start findTrainByStation select");
        List results = em.createQuery("select current_timestamp, \n" +
                "(t.departure + SUM(case when wayTime.id = wayA.id then 0 else wayTime.time end)) as departureTime \n" +
                "from Train t \n" +
                "join t.routeByIdRoute r\n" +
                "join r.schedulesById scheduleA\n" +
                "join scheduleA.wayByIdWay wayA\n" +
                "join wayA.stationByIdStation1 stationA\n" +
                "join r.schedulesById scheduleTime\n" +
                "join scheduleTime.wayByIdWay wayTime\n" +
                "where stationA.name = ?\n" +
                " and t.name = ?\n" +
                "and scheduleTime.seqNumber <= scheduleA.seqNumber\n" +
                "group by t.name\n")
                .setParameter(1, stationFrom.getName())
                .setParameter(2, train.getName())
                .getResultList();
        Timestamp current = (Timestamp) ((Object[])results.get(0))[0];
        current.setTime(current.getTime() + 10 * 60 * 1000);
        Timestamp departure = (Timestamp) ((Object[])results.get(0))[1];
        return results.size() == 0 ? false : current.before(departure);
    }

    public static Integer freeSeatsOnTrain(String train, String stationFrom, String stationTo) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("select t.totalSeats - count(ticket.id)\n" +
                "from Train t \n" +
                "inner join t.ticketsById ticket\n" +
                "where t.name = ?\n" +
                "group by t.id" +
                "")
                .setParameter(1, train)
                .getResultList();
        return results.size() == 1 ? (Integer) results.get(0) : -1;
    }

    public static boolean isAlreadyExistPassengerOnTrain(Train train, Passenger passenger) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start isAlreadyExistPassengerOnTrain select");
        List results = em.createQuery("select count(*)\n" +
                "from Train train \n" +
                "join train.ticketsById ticket\n" +
                "join ticket.passengerByIdPassenger passenger\n" +
                "where train.name = ?\n" +
                "and passenger.firstname = ?\n" +
                "and passenger.lastname = ?\n" +
                "and passenger.birthday = ?")
                .setParameter(1, train.getName())
                .setParameter(2, passenger.getFirstname())
                .setParameter(3, passenger.getLastname())
                .setParameter(4, passenger.getBirthday())
                .getResultList();
        return (Long) results.get(0) != 0;
    }

    public static HashMap<Integer, Integer[]> countOfPassengerOnEveryStation(Train train) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start countOfPassengerOnEveryStation select");
        List stationFrom = em.createQuery("select station.id, count(ticket.id), 0, schedule.seqNumber - 1 \n" +
                "from Train train \n" +
                "left join train.ticketsById ticket\n" +
                "inner join ticket.stationByStationFrom station\n" +
                "inner join train.routeByIdRoute route\n" +
                "inner join route.schedulesById schedule\n" +
                "inner join schedule.wayByIdWay way\n" +
                "inner join way.stationByIdStation1 stationFrom\n" +
                "where train.name = ?\n" +
                "and stationFrom.id = station.id\n" +
                " group by station.id")
                .setParameter(1, train.getName())
                .getResultList();
        List stationTo = em.createQuery("select station.id, 0, count(ticket.id), schedule.seqNumber \n" +
                "from Train train \n" +
                "left join train.ticketsById ticket\n" +
                "inner join ticket.stationByStationTo station\n" +
                "inner join train.routeByIdRoute route\n" +
                "inner join route.schedulesById schedule\n" +
                "inner join schedule.wayByIdWay way\n" +
                "inner join way.stationByIdStation2 stationTo\n" +
                "where train.name = ?\n" +
                "and stationTo.id = station.id\n" +
                " group by station.id")
                .setParameter(1, train.getName())
                .getResultList();
        HashMap<Integer, Integer[]> result = new HashMap<Integer, Integer[]>();
        for (Object[] obj : (List<Object[]>) stationFrom) {
            if (result.containsKey(obj[0])) {
                Integer[] a = result.get(obj[0]);
                a[0] += ((Long) obj[1]).intValue();
                a[1] += (Integer)obj[2];
                result.put((Integer) obj[0], new Integer[]{ a[1], a[2], (Integer) obj[3]});
            }  else {
                result.put((Integer) obj[0], new Integer[]{((Long) obj[1]).intValue(), (Integer) obj[2], (Integer) obj[3]});
            }
        }
        for (Object[] obj : (List<Object[]>) stationTo) {
            if (result.containsKey(obj[0])) {
                Integer[] a = result.get(obj[0]);
                a[0] += (Integer)obj[1];
                a[1] += ((Long) obj[2]).intValue();
                result.put((Integer) obj[0], new Integer[]{ a[1], a[2], (Integer) obj[3]});
            }  else {
                result.put((Integer) obj[0], new Integer[]{(Integer) obj[1], ((Long) obj[2]).intValue(), (Integer) obj[3]});
            }
        }
        return result;
    }

    public static List getAllStationsByTrain(Train train) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start getAllStationsByTrain select");
        List results = em.createNativeQuery("select distinct station.id, station.name, \n" +
                "case station.id\n" +
                "when  way.id_station1 \n" +
                "then schedule.seq_number - 1 \n" +
                "else schedule.seq_number\n" +
                "end as seq\n" +
                "from station\n" +
                "inner join way on (station.id = way.id_station1 or station.id = way.id_station2)\n" +
                "inner join schedule on schedule.id_way = way.id\n" +
                "inner join route on schedule.id_route = route.id\n" +
                "inner join train on route.id = train.id_route\n" +
                "where train.name =?\n" +
                "order by seq")
                .setParameter(1, train.getName())
                .getResultList();
        return results;
    }

    public static boolean saveNewTrain(Train train) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(train);

            trx.commit();
            log.debug("Saving train");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save train", e);
            trx.rollback();
            return false;
        }

    }

    public static List<Train> getAllTrains() {
        EntityManager em = emf.createEntityManager();
        log.debug("Start getAllTrains select");
        List<Train> results = em.createQuery("from Train ").getResultList();
        return results;

    }

//        public static void main(String[] args) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        List trains = findTrain("Nvk", "SPb", dateFormat.parse("2013-07-19") , dateFormat.parse("2013-10-19"));
//        System.out.println(trains.size());
//            System.out.println(countOfPassengerOnEveryStation("777"));
//    }
}