import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.util.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

public class Server extends UnicastRemoteObject implements ServerInterface, ActionListener{
    private String serverUrl;
    private List<ClientInterface> allClients;
    private double ci = 0.131825904205330;
    private double cr = -0.743643887036151;
    private double zoom = 1.5;
    private int Total = 0;
    private int id = 0;
    private JLabel userLabel;
    private JTextField ciLabel_1;
    private JTextField crLabel_1;
    private JTextField zoomLabel_1;
    private JButton submitButton;

    protected Server() throws RemoteException {
        //super();
        allClients = new ArrayList<>();
    }

    public void setGUI(LoadBalancerInterface loadbalancer, Server server){
        // Erstellung des JFrame und der GUI-Komponenten
        JFrame frame = new JFrame("Worker Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            try{
                loadbalancer.removeServer(server);
                for(Iterator<ClientInterface> iter = allClients.iterator(); iter.hasNext();){
                    ClientInterface clientInter = iter.next();
                    clientInter.shutDown();
                }
            }
            catch(Exception d){
                System.out.println(d);
            }
            System.exit(0);
        });
        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);
        JLabel userPort = new JLabel("URL: " + serverUrl, SwingConstants.CENTER);
        userLabel = new JLabel("Total User Online: " + Total, SwingConstants.CENTER);
        JLabel ciTextLabel = new JLabel("CI:");
        JLabel crTextLabel = new JLabel("CR:");
        JLabel zoomTextLabel = new JLabel("Zoom:");
        ciLabel_1 = new JTextField(Double.toString(ci), SwingConstants.CENTER);
        crLabel_1 = new JTextField(Double.toString(cr), SwingConstants.CENTER);
        zoomLabel_1 = new JTextField(Double.toString(zoom), SwingConstants.CENTER);
        
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        
        // Hinzufügen der Komponenten zum JPanel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(userPort);
        panel.add(userLabel);
        panel.add(ciTextLabel);
        panel.add(ciLabel_1);
        panel.add(crTextLabel);
        panel.add(crLabel_1);
        panel.add(zoomTextLabel);
        panel.add(zoomLabel_1);
        panel.add(submitButton);
        panel.add(exitButton);

        // Hinzufügen des JPanel zum JFrame und Anzeigen des Frames
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void setUrl(String serverUrl){
        this.serverUrl = serverUrl;
    }

    @Override
    public String getUrl() throws RemoteException {
        return serverUrl;
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {
        /* if (!allClients.contains(client)) {
            allClients.add(client);
            System.out.println("New Client "+ client.getId() +" registered");
        } */

        String id_u = client.getId();

        System.out.println("new UserID: " + id_u);
        
        allClients.add(client);

        Total++;

        System.out.println(Total);
        userLabel.setText("Total User Online: " + Total);
        /* for(Iterator<ClientInterface> iter = allClients.iterator(); iter.hasNext();){
            ClientInterface clientInter = iter.next();
            clientInter.print("Total User Online: "+ Integer.toString(Total));
        } */
    }

    @Override
    public void broadcastMessage(String message) throws RemoteException {
        //System.out.println("Broadcasting message: " + message);
        for (ClientInterface client : allClients) { //Schickt das Nachricht an alle Kunden
            client.receiveMessage(message);
        }
    }

    //###########################################################################################

    /* public synchronized void addClient(ClientInterface objRef) throws RemoteException{
        String id_u = objRef.getId();

        System.out.println("new UserID: " + id_u);
        
        allClients.add(objRef);

        Total++;

        System.out.println(Total);
        userLabel.setText("Total User Online: " + Total);
        for(Iterator<ClientInterface> iter = allClients.iterator(); iter.hasNext();){
            ClientInterface client = iter.next();
            client.print("Total User Online: "+ Integer.toString(Total));
        }
    } */

    public synchronized int ask_id() throws RemoteException{
        id = id + 1;
        return id;
    }

    public synchronized void removeUser(ClientInterface objRef) throws RemoteException{
        String id_u = objRef.getId();
        System.out.println("ClientID: "+ id_u + " is removed");
        allClients.remove(objRef);
        Total--;
        userLabel.setText("User: " + Total);
        for(Iterator<ClientInterface> iter = allClients.iterator(); iter.hasNext();){
            ClientInterface client = iter.next();
            client.print("Total User Online: "+ Integer.toString(Total));
        }
    }

    private synchronized void tellUser(double ci, double cr, double zoom) throws RemoteException{
        for(Iterator<ClientInterface> iter = allClients.iterator(); iter.hasNext();){
            ClientInterface client = iter.next();
            client.print("NEW VALUE \n CI: "+Double.toString(ci) + "  CR: "+Double.toString(cr) + "  ZOOM: "+Double.toString(zoom));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton sourceButton = (JButton) e.getSource();
            if (sourceButton.getText().equals("Submit")) {
                String ciText = ciLabel_1.getText();
                String crText = crLabel_1.getText();
                String zoomText = zoomLabel_1.getText();
                try {
                    double ciValue = Double.parseDouble(ciText);
                    double crValue = Double.parseDouble(crText);
                    double zoomValue = Double.parseDouble(zoomText);
                    ci = ciValue;
                    cr = crValue;
                    zoom = zoomValue;
                    try {
                        tellUser(ci,cr,zoom);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Value of CI: " + ciValue);
                    System.out.println("Value of CR: " + crValue);
                    System.out.println("Value of ZOOM: " + zoomValue);
                } catch (NumberFormatException ex) {
                    System.out.println("Input invalid");
                }
            }
        }
    }




    @Override
    public synchronized double req_ci() throws RemoteException {
        return ci;
    }
    @Override
    public synchronized double req_cr() throws RemoteException {
        return cr;
    }

    @Override
    public synchronized double req_zoom() throws RemoteException {
        return zoom;
    }
    
    @Override
    public synchronized double send_funk1(double a, double b) throws RemoteException {
        double c = a - b;
        return c;
    }

    @Override
    public synchronized double send_funk2(double a, double b, double z) throws RemoteException {
        double c = a - b / (2 * z);
        return c;
    }

    @Override
    public synchronized double send_funk3(double a, double b, double z) throws RemoteException {
        double c = a + b / (2 * z);
        return c;
    }


    //##############################################################################################

    public static void main(String[] args) {
        try {
            Server server = new Server();
            LoadBalancerInterface loadbalancer = (LoadBalancerInterface) java.rmi.registry.LocateRegistry.getRegistry("localhost", 2000).lookup("LoadBalancerServer");

            int serverPort = loadbalancer.getPort();

            InetAddress localhost = InetAddress.getLocalHost();
            String currentIP = localhost.getHostAddress(); //"0.0.0.0";
            String link = "rmi://" + currentIP + ":" + serverPort + "/WorkerServer";

            //java.rmi.registry.LocateRegistry.createRegistry(serverPort).rebind(link, server); geht nicht!
            //java.rmi.registry.LocateRegistry.createRegistry(serverPort).rebind("WorkerServer", server);
            java.rmi.registry.LocateRegistry.createRegistry(serverPort).rebind("WorkerServer", server);

            server.setUrl(link);
            server.setGUI(loadbalancer, server);
            loadbalancer.registerServer(server);
            System.out.println("Server ist gestartet...\n" + link + "\n\n");
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
    }
}