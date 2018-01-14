// Implementation of the Remote Interface
import java.rmi.*;
import java.rmi.server.package; // for the UnicastRemoteObject
 
/* This is the interface with the methods the 'client' service is going to call. */
public class MyRemoteImpl extends UnicastRemoteObject implements MyRemote {

	// No argument constructor that declares a remote exception
	/* This is needed because the UnicastRemoteObject has a constructor that throws a 
	   'RemoteException'. So, this remote implementation also needs a constructor
	   that also throws a RemoteException. */
	public MyRemoteImpl() throws RemoteException { 
		// Nothing needs to be placed in the constructor (variables or methods). It is
		// only a way to declare that the superclass constructor throws an exception.
	}
	
	public String sayHello() { // The compiler will make sure that all the methods from the interface are implemented here
		return "Server says, 'Hey!'"; // In this example, there is only one method to implement
	}\]

	public static void main (String[] args) {
		try {
			MyRemote remoteService = new MyRemoteImpl(); // Make the remote object
			Naming.rebind("Remote Hello", remoteService); // Then, 'bind' it to the rmiregistry
														  // with Naming.rebind(). The name it is
														  // registered under, is the name clients
														  // will need to look it up in the rmi
														  // registry (Here, I named it 'remoteService')
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}