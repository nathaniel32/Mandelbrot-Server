import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Client extends UnicastRemoteObject implements ClientInterface {
    public String id;

    protected Client(String id) throws RemoteException {
        //super();
        this.id = id;
    }

    @Override
    public void print(String message) throws RemoteException{
        System.out.println(message);
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println("Global Message: " + message);
    }

    @Override
    public void shutDown() throws RemoteException {
        System.out.println("Server heruntergefahren");
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                Random random = new Random();
                int randomNumber = random.nextInt(100);
                
                Client client = new Client("CL" + randomNumber);
    
                LoadBalancerInterface loadbalancer = (LoadBalancerInterface) java.rmi.registry.LocateRegistry.getRegistry(args[0], 2000).lookup("LoadBalancerServer");
                String serverUrl = loadbalancer.getBalanceServerUrl();
    
                System.out.println(serverUrl);
    
                //ServerInterface server = (ServerInterface) java.rmi.registry.LocateRegistry.getRegistry("localhost", 9000).lookup("ChatServer");
                //ServerInterface server = (ServerInterface) java.rmi.registry.LocateRegistry.getRegistry("localhost", 9000).lookup("ChatServer");
                //ServerInterface server = (ServerInterface) java.rmi.registry.LocateRegistry.getRegistry(serverUrl);
                ServerInterface server = (ServerInterface) Naming.lookup(serverUrl);
                server.registerClient(client);
                System.out.println("Client ID: "+ client.getId() +".\nSie haben eine Verbindung zum Server-Url: " + serverUrl + " hergestellt\n\n");
                server.broadcastMessage(client.getId() + " Connected");
    
                ApfelPresenter p = new ApfelPresenter();
                ApfelView v = new ApfelView(p);
                ApfelModel m = new ApfelModel(v);
                p.setModelAndView(m, v, server, client);
                p.apfel(server);
            } catch (Exception e) {
                System.err.println("Client exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: IP");
        }
    }
}



class ApfelPresenter {
    protected ApfelModel m;
    protected ApfelView v;

    double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1;
    int xpix = 640, ypix = 480;

    public void setModelAndView(ApfelModel m, ApfelView v, ServerInterface server, Client client) {
        this.m = m;
        this.v = v;
        v.setDim(xpix, ypix, server, client);
        m.setParameter(xpix, ypix);
    }

    void apfel(ServerInterface server) {
        Color[][] c = new Color[xpix][ypix];
        Ticker Mandelzeit = new Ticker(v);
        for (int i = 1; i < 65; i++) {
            v.vergro(i + " Vergrößerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);

            c = m.apfel_bild(xmin, xmax, ymin, ymax);
            v.update(c); //repaint
            try {
                double cr = server.req_cr();
                double ci = server.req_ci();
                double zoomRate = server.req_zoom();

                double xdim = server.send_funk1(xmax, xmin);
                double ydim = server.send_funk1(ymax, ymin);

                xmin = server.send_funk2(cr, xdim, zoomRate);
                ymin = server.send_funk2(ci, ydim, zoomRate);

                xmax = server.send_funk3(cr, xdim, zoomRate);
                ymax = server.send_funk3(ci, ydim, zoomRate);

                v.setT(ci,cr,zoomRate);
            } catch (RemoteException e) {
                //e.printStackTrace();
                System.out.println("Lost connection");
            }
        }
        Mandelzeit.interrupt();
        System.out.println("END");
    }
}


class ApfelView {
    ApfelPresenter p;
    private ApfelPanel ap = new ApfelPanel();
    private JLabel lfi;
    private JLabel lfr;
    private JLabel lfz;
    private JLabel ServerPort;
    private JLabel NoID;
    private JLabel timeCPU;
    private JLabel zahl_vergr;

    int xpix, ypix;
    BufferedImage image;

    public ApfelView(ApfelPresenter p) {
        this.p = p;
    }

    public void setT(double ci, double cr, double zoom) {
        lfi.setText("CI: " +Double.toString(ci));
        lfr.setText("CR: " +Double.toString(cr));
        lfz.setText("Zoom: " +Double.toString(zoom));
    }

    public void setTime(long time) {
        timeCPU.setText("Time: " +Long.toString(time)+" Second");
    }

    public void vergro(String zahl) {
        zahl_vergr.setText(zahl);
    }

    public void setDim(int xpix, int ypix, ServerInterface server, Client client) {
        this.xpix = xpix;
        this.ypix = ypix;
        image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
        initView(server, client);
    }

    private void initView(ServerInterface server, Client client) {
        JFrame f = new JFrame("Mandelbrot");
        JPanel  sp = new JPanel( new FlowLayout());
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            try{
                server.removeUser(client);
            }
            catch(Exception d){
                System.out.println(d);
            }
            System.exit(0);
        });
        
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    server.removeUser(client);
                }
                catch(Exception d){
                    System.out.println(d);
                }
            }
        });
        timeCPU = new JLabel("Time: 0 Second");
        zahl_vergr = new JLabel("");
        String myServerUrl = null;
        try {
            myServerUrl = server.getUrl();
        }catch (Exception e) {
            System.err.println("URL ERROR:");
        }
        ServerPort = new JLabel("URL: "+ myServerUrl);
        NoID = new JLabel("User ID: "+ client.id);
        try {
            double va_zoom = server.req_zoom();
            double va_ci = server.req_ci();
            double va_cr = server.req_cr();

            lfr = new JLabel("CR: " +Double.toString(va_cr));
            lfi = new JLabel("CI: " +Double.toString(va_ci));
            lfz = new JLabel("Zoom: " +Double.toString(va_zoom));
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        sp.add(ServerPort);
        sp.add(NoID);
        sp.add(Box.createVerticalStrut(5));
        sp.add(timeCPU);
        sp.add(Box.createVerticalStrut(5));
        sp.add(zahl_vergr);
        sp.add(Box.createVerticalStrut(10));
        sp.add(lfr);
        sp.add(lfi);
        sp.add(lfz);
        sp.add(Box.createVerticalStrut(10));
        sp.add(exitButton);

        f.add(ap, BorderLayout.CENTER);
        f.add(sp, BorderLayout.SOUTH);
        f.setSize(xpix, ypix+150);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void update(Color[][] c) {
        for (int y = 0; y < ypix; y++) {
            for (int x = 0; x < xpix; x++) {
            if (c[x][y] != null) image.setRGB(x, y, c[x][y].getRGB());
            }
        }
        ap.repaint();
    }

    class ApfelPanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }
}

class ApfelModel {
    ApfelView v;
    final int max_iter = 5000;
    final double max_betrag2 = 4;
    int xpix, ypix;
    double xmin, xmax, ymin, ymax;
    int[][] bildIter;
    Color[][] bild;

    public ApfelModel(ApfelView v) {
        this.v = v;
    }

    public void setParameter(int xpix, int ypix) {
        this.xpix = xpix;
        this.ypix = ypix;
        bildIter = new int[xpix][ypix];
        bild = new Color[xpix][ypix];
    }


    Color[][] apfel_bild(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;

        ApfelWorker worker = new ApfelWorker(0, ypix);
        worker.work();
        return bild;
    }

    class ApfelWorker  {
        int y_sta, y_sto;

        public ApfelWorker(int y_start, int y_stopp) {
            this.y_sta = y_start;
            this.y_sto = y_stopp;
        }

        public void work() {
            double c_re, c_im;
            for (int y = y_sta; y < y_sto; y++) {
                c_im = ymin + (ymax - ymin) * y / ypix;

                for (int x = 0; x < xpix; x++) {
                    c_re = xmin + (xmax - xmin) * x / xpix;
                    int iter = calc(c_re, c_im);
                    bildIter[x][y] = iter;
                    if (iter == max_iter){
                        Color pix = Color.BLACK;
                        bild[x][y] = pix;
                    }
                    else {
                        float c = (float) iter / max_iter;
                        Color pix = Color.getHSBColor(c, 1f, 1f);
                        v.image.setRGB(x, y, pix.getRGB());
                        bild[x][y] = pix;
                    }
                }
            }
        }

        public int calc(double cr, double ci) {
            int iter;
            double zr, zi, zr2 = 0, zi2 = 0, zri = 0, betrag2 = 0;
            for (iter = 0; iter < max_iter && betrag2 <= max_betrag2; iter++) {
                zr = zr2 - zi2 + cr;
                zi = zri + zri + ci;

                zr2 = zr * zr;
                zi2 = zi * zi;
                zri = zr * zi;
                betrag2 = zr2 + zi2;
            }
            return iter;
        }
    }
}

class Ticker extends Thread{
    private final static long UPDATE_INTERVAL = 10;
    protected ApfelView v;
    private long startTime = System.currentTimeMillis()/1000;
    private long cpuTime_sec;

    public Ticker(ApfelView v) {
        this.v = v;
        start();
    }

    public void run() {
        try {
            while(!isInterrupted()) {
                cpuTime_sec = System.currentTimeMillis()/1000;
                v.setTime(cpuTime_sec-startTime);
                Thread.sleep(UPDATE_INTERVAL);
            }
        }
        catch(InterruptedException e) {
            System.out.println("Error!");
        }
    }
}