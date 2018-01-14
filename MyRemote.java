// Creating the Remote Interface
import java.rmi.*; // The "Remote" interface is in jthe sava.rmi package

/* Remote is only a 'marker' interface, which means it has tos no methods. It has special meaning for
  RMI, though, so it is a rule that must be followed. */

public interface MyRemote extends Remote { // It has to announce that it's for a remoote method call
	
	// All methods must declare that they 'throw a RemoteException'
	/*  Remote interface is the one the client uses as the polymorphic type for the service. In other words
	    the client invokes methods on something tha timplements the remote interface. That something is the stub, 
	    and since the stub is doing networking and I/O, all kinds of bad thinds can happen. The client has to
	    acknowledge the risks by handling or declaring the remote exceptions. If the methods in an interface declare
	    exceptions, any code calling methods on a reference of that type (the interface type) must handle or declare 
	    the exceptions.  */

	public String sayHello() throws RemoteException; // Every remote methods call is considered 'risky'. Declaring 
													 // RemoteExpection on very method forces the client to pay attention
													 // and acnowledge that things might not work.
													 // Also, arguments and return values must be primitive types or Serializable.
													 // This is because any argument to a remote method has to be packaged up and
													 // shipped across the network, and that's done through Serialization.
	
}