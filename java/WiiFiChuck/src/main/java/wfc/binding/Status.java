package wfc.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

/**
 * Created by Ljoha on 7/14/2015.
 */
public class Status extends StringBinding {
    private StringProperty portName;
    private StringProperty status;

    public Status(StringProperty portName, StringProperty status) {
        this.portName = portName;
        this.status = status;
        this.bind(this.portName);
        this.bind(this.status);
    }

    @Override
    protected String computeValue() {
        return this.status.get();
    }
}
