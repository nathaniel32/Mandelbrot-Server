import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    void registerServer(ServerInterface client) throws RemoteException;
    int getPort() throws RemoteException;
    String getBalanceServerUrl() throws RemoteException;
    void removeServer(ServerInterface server) throws RemoteException;
}