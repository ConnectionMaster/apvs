package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.client.tablet.CameraUI;
import ch.cern.atlas.apvs.client.tablet.ImageUI;
import ch.cern.atlas.apvs.client.tablet.MainMenuUI;
import ch.cern.atlas.apvs.client.tablet.ModelUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureNavigator;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.client.ui.PtuSelector;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {

	RemoteEventBus getEventBus();

	PlaceController getPlaceController();

	FileServiceAsync getFileService();

	PtuServiceAsync getPtuService();

	MainMenuUI getHomeView();

	ModelUI getModelView();

	CameraUI getCameraView(int type);

	ProcedureMenuUI getProcedureMenuView();

	ImageUI getImagePanel(String url);

	ProcedureUI getProcedurePanel(String url, String name, String step);

	PtuSelector getPtuSelector();

	MeasurementView getMeasurementView();

	ProcedureNavigator getProcedureNavigator();

	ProcedureView getProcedureView(int width, int height);

	ProcedureView getProcedureView(int width, int height, String url, String name, String step);

}
