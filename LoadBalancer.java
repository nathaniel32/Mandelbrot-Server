import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
//import java.util.Random;

public class LoadBalancer extends UnicastRemoteObject implements LoadBalancerInterface {
    private List<ServerInterface> allServer;
    private int portServer = 9000;
    private int fillServerIndex = 0;

    public LoadBalancer() throws RemoteException {
        //super();
        allServer = new ArrayList<>();
    }

    @Override
    public synchronized int getPort() throws RemoteException {
        return this.portServer++;
    }

    @Override
    public String getBalanceServerUrl() throws RemoteException {
        //Random random = new Random();
        //int serverIndex = random.nextInt(allServer.size());

        String thisUrl = allServer.get(fillServerIndex).getUrl();

        /* if(fillServerIndex > allServer.size() - 2){
            fillServerIndex = 0;
        }else{
            fillServerIndex++;
        } */
        
        fillServerIndex = (fillServerIndex + 1) % allServer.size();

        System.out.println("Client wird " + thisUrl + " beitreten");

        return thisUrl;
    }

    @Override
    public void registerServer(ServerInterface server) throws RemoteException {
        if (!allServer.contains(server)) {
            allServer.add(server);
            System.out.println("New Server Port: " + server.getUrl());
        }
    }

    @Override
    public synchronized void removeServer(ServerInterface server) throws RemoteException{
        String serverUrl = server.getUrl();
        System.out.println("Server Port: "+ serverUrl + " is removed");
        allServer.remove(server);
    }

    public static void main(String[] args) {
        try {
            LoadBalancer loadbalancerserver = new LoadBalancer();
            java.rmi.registry.LocateRegistry.createRegistry(2000).rebind("LoadBalancerServer", loadbalancerserver);
            System.out.println("Load Balancer ist gestartet...");
        } catch (Exception e) {
            System.err.println("Load Balancer exception:");
            e.printStackTrace();
        }
    }
}
