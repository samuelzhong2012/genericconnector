package ch.maxant.generic_jca_adapter;

import java.io.File;
import java.util.logging.Level;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/** the XA resource used by Java SE */
public class MicroserviceXAResource extends AbstractTransactionAssistanceXAResource {

	private static final long serialVersionUID = 1L;

	private final UnderlyingConnectionImpl underlyingConnection;

	/** how old do transactions have to be before we consider cleaning them up? 
	 * since recovery runs async, we need to be careful not to start recovering something thats still active!
	 * TODO could we ask BTM for the active transactions, and skip those ones? */
	private static long minAgeOfTransactionInMSBeforeRelevantForRecovery = 30000L;

	/** where should we store our information about transaction state? */
	private static File recoveryStatePersistenceDirectory = new File(".");

	private String jndiName;

	/** @param jndiName 
	 * @param commitRollbackCallback A stateless callback handler, which can be used for committing or rolling back any transaction */
	public MicroserviceXAResource(String jndiName, UnderlyingConnectionImpl underlyingConnection) {
		this.jndiName = jndiName;
		this.underlyingConnection = underlyingConnection;
	}

	@Override
	protected long getMinAgeOfTransactionBeforeRelevantForRecovery() {
		return minAgeOfTransactionInMSBeforeRelevantForRecovery;
	}
	
	@Override
	protected File getRecoveryStatePersistenceDirectory() {
		return recoveryStatePersistenceDirectory;
	}
	
	@Override
	protected UnderlyingConnection getUnderlyingConnection() {
		return underlyingConnection;
	}

	@Override
	public boolean isSameRM(XAResource xares) throws XAException {
		log.log(Level.FINEST, "isSameRM " + xares);
		
        if(xares instanceof MicroserviceXAResource){
        	MicroserviceXAResource other = (MicroserviceXAResource) xares;
            if(this.jndiName.equals(other.jndiName)){
                return true;
            }
        }
        return false;
		// TODO hmm not sure about this
	}

	/** execute the given callback in the active transaction */
	public <T> T executeInActiveTransaction(ExecuteCallback<T> c) throws Exception {
		return underlyingConnection.execute(c, recoveryStatePersistenceDirectory);
	}

	/** setup the location of where recovery state is stored, and how old it must be before recovery is attempted. defaults are the working directory and 30 seconds. */
	public static void configure(long minAgeOfTransactionInMSBeforeRelevantForRecovery, File recoveryStatePersistenceDirectory){
		MicroserviceXAResource.minAgeOfTransactionInMSBeforeRelevantForRecovery = minAgeOfTransactionInMSBeforeRelevantForRecovery;
		MicroserviceXAResource.recoveryStatePersistenceDirectory = recoveryStatePersistenceDirectory;
	}

	/** the stateless callback, which can be used for committing or rolling back any transaction */
	CommitRollbackCallback getCommitRollbackCallback() {
		return underlyingConnection;
	}

	public String getJndiName() {
		return jndiName;
	}
}