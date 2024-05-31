import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    String getId() throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
    public void print(String message) throws java.rmi.RemoteException;
    void shutDown() throws RemoteException;
}