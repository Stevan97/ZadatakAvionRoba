package zadaci;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import model.Avion;
import model.Roba;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvionNit extends Thread{

    static Dao<Avion,Integer> avionDao;
    static Dao<Roba,Integer> robaDao;

    public static boolean daLiJeDozvoljenoPoletanje = true;

    public static final Object sinhr = new Object();

    Avion avion = null;

    public AvionNit(Avion avion) {
        this.avion = avion;
    }





    protected void provera() {

        System.out.println("Provera za poletanje" + avion.getId());

        try {
            sleep(0 + (int)Math.random() * 2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected void proveraZaDozvolu() {

        boolean isDozvoljeno;
        do {
           System.out.println("Provera da li moze da poleti" + avion.getId());
            synchronized (sinhr) {
               if (daLiJeDozvoljenoPoletanje) {
                   daLiJeDozvoljenoPoletanje = false;
                   isDozvoljeno = true;
               } else {
                   isDozvoljeno = false;
               }

            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (!isDozvoljeno);


    }

    protected void poleti() {

        System.out.println("Avion je izasao na pistu i polece!" + avion.getId());



        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Avion poleteo" + avion.getId());

        synchronized (sinhr) {
            daLiJeDozvoljenoPoletanje = true;
        }

    }

    public void run() {
        provera();
        proveraZaDozvolu();
        poleti();
    }

    public static void main(String[] args) {
        ConnectionSource connectionSource = null;

        try {
            connectionSource = new JdbcConnectionSource(Konstante.DATABASE_URL);

            avionDao = DaoManager.createDao(connectionSource,Avion.class);
            robaDao = DaoManager.createDao(connectionSource,Roba.class);

            ArrayList<Thread> threads=new ArrayList<Thread>();
            List<Avion> avioni = avionDao.queryForAll();
            for (Avion a: avioni) {
               Thread t = (new AvionNit(a));
                threads.add(t);
                t.start();

            }

            try {
                for (Thread t: threads) {
                    t.join();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            System.out.println("Svi avionu su poleteli");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




}
