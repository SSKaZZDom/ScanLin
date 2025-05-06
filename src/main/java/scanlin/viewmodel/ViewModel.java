package scanlin.viewmodel;

import scanlin.model.ModelInterface;
import scanlin.view.ViewInterface;

public class ViewModel implements ViewModelInterface {
    ModelInterface model;
    ViewInterface view;
    public ViewModel(ModelInterface model, ViewInterface view) {
        this.model = model;
        this.view = view;
    }
}
