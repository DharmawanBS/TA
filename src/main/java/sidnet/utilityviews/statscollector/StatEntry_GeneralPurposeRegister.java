package sidnet.utilityviews.statscollector;

public class StatEntry_GeneralPurposeRegister
extends AggregateStatEntry{
    private static final String TAG = "Contor";

    public StatEntry_GeneralPurposeRegister(String key) {
        super(key, TAG);
    }
    
    public void reset() {
    	// Not implemented
    }
              
    /**
     * @inheridoc
     */
    public String getValueAsString() {
        return "" + value;
    }    
}
