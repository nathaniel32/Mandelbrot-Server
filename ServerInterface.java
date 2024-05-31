import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    String getUrl() throws RemoteException;
    void registerClient(ClientInterface client) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;


    //public void addClient(ClientInterface objRef) throws RemoteException;
    
    public void removeUser(ClientInterface objRef) throws RemoteException;

    double send_funk1(double a, double b) throws RemoteException;
    double send_funk2(double a, double b, double z) throws RemoteException;
    double send_funk3(double a, double b, double z) throws RemoteException;

    double req_cr() throws RemoteException;
    double req_ci() throws RemoteException;
    double req_zoom() throws RemoteException;

    int ask_id() throws RemoteException;
}