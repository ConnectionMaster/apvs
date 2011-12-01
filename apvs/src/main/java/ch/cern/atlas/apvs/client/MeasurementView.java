package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuChangedEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MeasurementView extends SimplePanel {

	private static NumberFormat format = NumberFormat.getFormat("0.00");

	private ListDataProvider<Measurement<?>> dataProvider = new ListDataProvider<Measurement<?>>();
	private CellTable<Measurement<?>> table = new CellTable<Measurement<?>>();
	private ListHandler<Measurement<?>> columnSortHandler;

	private DosimeterServiceAsync dosimeterService;

	private Dosimeter dosimeter = new Dosimeter();
	private int serialNo = 0;

	private HandlerRegistration registration;
	private PtuServiceAsync ptuService;

	private SimpleRemoteEventBus eventBus;
	private Ptu ptu = new Ptu();

	public MeasurementView(SimpleRemoteEventBus eventBus,
			DosimeterServiceAsync dosimeterService, PtuServiceAsync ptuService) {
		this.eventBus = eventBus;
		this.ptuService = ptuService;
		this.dosimeterService = dosimeterService;

		add(table);

		SelectDosimeterEvent.register(eventBus,
				new SelectDosimeterEvent.Handler() {

					@Override
					public void onDosimeterSelected(SelectDosimeterEvent event) {
						serialNo = event.getSerialNo();
						getDosimeter();
					}
				});

		SelectPtuEvent.register(eventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				getPtu(event.getPtuId());
			}
		});

		TextColumn<Measurement<?>> name = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				return object.getName();
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String name = getValue(object);
				if (name != null) {
					((TextCell) getCell()).render(context,
							SafeHtmlUtils.fromSafeConstant(name), sb);
				}
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		TextColumn<Measurement<?>> value = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				if (object == null) {
					return "";
				}
				return format.format((Double) object.getValue());
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String s = getValue(object);
				if ((object != null)
						&& object.getName().equals(ptu.getLastChanged())) {
					String a;
					switch (ptu.getState()) {
					case NEW:
						a = "&larr;";
						break;
					case UP:
						a = "&uarr;";
						break;
					case DOWN:
						a = "&darr;";
						break;
					default:
						a = "";
						break;
					}
					s = a + "&nbsp;<b>" + s + "</b>";
				}
				((TextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(s), sb);
			}

		};
		value.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(value, "Value");

		TextColumn<Measurement<?>> unit = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				return object.getUnit();
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String unit = getValue(object);
				if (unit != null) {
					((TextCell) getCell()).render(context,
							SafeHtmlUtils.fromSafeConstant(unit), sb);
				}
			}
		};
		unit.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unit.setSortable(true);
		table.addColumn(unit, "Unit");

		List<Measurement<?>> list = new ArrayList<Measurement<?>>();
		dataProvider.addDataDisplay(table);
		dataProvider.setList(list);

		columnSortHandler = new ListHandler<Measurement<?>>(
				dataProvider.getList());
		columnSortHandler.setComparator(name,
				new Comparator<Measurement<? extends Object>>() {
					public int compare(Measurement<? extends Object> o1,
							Measurement<? extends Object> o2) {
						if (o1 == o2) {
							return 0;
						}

						if (o1 != null) {
							return (o2 != null) ? o1.getName().compareTo(
									o2.getName()) : 1;
						}
						return -1;
					}
				});
		columnSortHandler.setComparator(value,
				new Comparator<Measurement<?>>() {
					public int compare(Measurement<?> o1, Measurement<?> o2) {
						if (o1 == o2) {
							return 0;
						}

						if ((o1 != null)
								&& (o1.getValue() instanceof Comparable<?>)) {
							@SuppressWarnings("unchecked")
							Comparable<Object> value = (Comparable<Object>) o1
									.getValue();
							return (o2 != null) ? value.compareTo(o2.getValue())
									: 1;
						}
						return -1;
					}
				});
		columnSortHandler.setComparator(unit,
				new Comparator<Measurement<? extends Object>>() {
					public int compare(Measurement<? extends Object> o1,
							Measurement<? extends Object> o2) {
						if (o1 == o2) {
							return 0;
						}

						if (o1 != null) {
							return (o2 != null) ? o1.getUnit().compareTo(
									o2.getUnit()) : 1;
						}
						return -1;
					}
				});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(name);
	}

	private void getDosimeter() {
		dosimeterService.getDosimeter(serialNo, (long) dosimeter.hashCode(),
				new AsyncCallback<Dosimeter>() {

					@Override
					public void onSuccess(Dosimeter result) {
						if (result == null) {
							System.err
									.println("FIXME onSuccess null in measurementView");
							return;
						}

						if (serialNo != result.getSerialNo()) {
							System.err.println("SerialNo "
									+ result.getSerialNo() + " != " + serialNo
									+ ", abandoned");
							return;
						}

						dosimeter = result;

						// FIXME we always get two, we need to replace the
						// previous ones...
						List<Measurement<?>> list = dataProvider.getList();
						list.set(
								0,
								new Measurement<Double>("Dose", dosimeter
										.getDose(), "&micro;Sv"));
						list.set(
								1,
								new Measurement<Double>("Rate", dosimeter
										.getRate(), "&micro;Sv/h"));

						// Resort the table
						ColumnSortEvent.fire(table, table.getColumnSortList());
						table.redraw();

						getDosimeter();
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Could not retrieve dosimeter");

						getDosimeter();
					}
				});

	}

	private void getPtu(final int ptuId) {
		ptuService.getPtu(ptuId, (long) ptu.hashCode(),
				new AsyncCallback<Ptu>() {

					@Override
					public void onSuccess(Ptu result) {
						setPtu(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Could not retrieve ptu " + ptuId);
					}
				});

	}

	private void setPtu(Ptu newPtu) {
		if (registration != null) {
			registration.removeHandler();
			registration = null;
		}

		if (newPtu == null) {
			ptu = new Ptu();
			System.err.println("======> PTU = " + null);
		} else {
			ptu = newPtu;
			System.err.println("======> PTU = " + ptu.getPtuId());

			registration = PtuChangedEvent.register(eventBus,
					new PtuChangedEvent.Handler() {

						@Override
						public void onPtuChanged(PtuChangedEvent event) {
							if (event.getPtu().getPtuId() == ptu.getPtuId()) {
								ptu = event.getPtu();
								updatePtu();
							} else {
								System.err.println("Ignoring "+event.getPtu().getPtuId());
							}
						}
					});
		}

		updatePtu();
	}

	private void updatePtu() {
		System.err.println("Updating PTU "+ptu.getPtuId());
		// FIXME maybe a better way
		List<Measurement<?>> list = dataProvider.getList();
		list.clear();

		list.addAll(ptu.getMeasurements());

		// Resort the table
		ColumnSortEvent.fire(table, table.getColumnSortList());
		table.redraw();
	}
}
