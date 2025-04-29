package scanlin.viewmodel;

import scanlin.model.ModelInterface;

public class ViewModel implements ViewModelInterface {
    ModelInterface model;
    public ViewModel(ModelInterface model) {
        this.model = model;
    }
}
