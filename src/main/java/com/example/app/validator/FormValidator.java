package com.example.app.validator;

import com.example.app.component.FormItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Holds field validators and global rules.
 * Each field is associated with a FormItem to show errors.
 */
public class FormValidator {

    private static class FieldWrap {
        FieldValidator v; FormItem item;
        FieldWrap(FieldValidator v, FormItem item) { this.v = v; this.item = item; }
    }

    private final List<FieldWrap> fields = new ArrayList<>();
    private final List<Supplier<String>> globals = new ArrayList<>();

    public void addField(FieldValidator fv, FormItem item) {
        fields.add(new FieldWrap(fv, item));
    }

    public void addGlobalRule(Supplier<String> r) { globals.add(r); }

    public boolean validateAll() {
        boolean ok = true;
        // per-field
        for (FieldWrap f : fields) {
            String msg = f.v.validate();
            if (msg != null) {
                f.item.showError(msg);
                ok = false;
            } else {
                f.item.showError(null);
            }
        }
        // global
        for (Supplier<String> g : globals) {
            String msg = g.get();
            if (msg != null) {
                // show first global error on first field for visibility
                if (!fields.isEmpty()) fields.get(0).item.showError(msg);
                ok = false;
            }
        }
        return ok;
    }
}
