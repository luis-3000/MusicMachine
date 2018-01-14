import java.rmi.*; 

public class MyRemoteClient {
	
	public static void main (String[] args) {
		new MyRemoteClient().go();
	}

	public void go() {
		try {
			// Need the cast to MyRemote since the 'thing' returned by the lookup() method comes out of the registry
			// as type Object. The IP address or hostname is needed, as well as the name used to bind/rebind the service
			MyRemote remoteService = (MyRemote) Naming.lookup("rmi://127.0.0.1/Remote Hello");
			String s = remoteService.sayHello(); // This looks like a regular old method call! Except that is must
												// acknowledge the RemoteException
			System.out.println(s);
		} catch {

		}
	}
}