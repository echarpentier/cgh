package fr.pfgen.cgh.client.datasources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.client.services.GenericGwtRpcServiceAsync;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.viewer.DetailViewerRecord;

/**
 * Generic abstract {@link GwtRpcDataSource} implementation, supporting server-side paging and sorting.
 * Extend this class if you want to create a GWT RPC DataSource for SmartGWT. This is based on the
 * {@link GwtRpcDataSource} example provided in the smartgwt-extensions.
 * 
 * In order to use this class, you have to implement both
 * {@link GenericGwtRpcService} and {@link GenericGwtRpcServiceAsync}
 * provided in the same package. To use paging ({@link FetchMode#PAGED}), you'll have to return
 * a {@link GenericGwtRpcList} from the fetch()-method of your {@link GenericGwtRpcService}-implementation.
 * 
 * @param <D>
 *            type of the transfer object holding the data (will most likely be
 *            a simple POJO), must implement {@link Serializable} or {@link IsSerializable}.
 * @param <R>
 *            any extension of {@link Record}, such as {@link ListGridRecord},
 *            {@link DetailViewerRecord} or {@link TreeNode} to use with your SmartGWT widget.
 * @param <SA>
 *            the asynchronous version of your service. Extend
 *            {@link GenericGwtRpcService} and then 
 *            {@link GenericGwtRpcServiceAsync} to implement it.
 * 
 * @see GwtRpcDataSource
 * @see GenericGwtRpcService
 * @see GenericGwtRpcServiceAsync
 * @see GenericGwtRpcList
 * 
 * @author Francois Marbot
 * @author Aleksandras Novikovas
 * @author System Tier
 * @version 1.1
 */
public abstract class GenericGwtRpcDataSource<D, R extends Record, SA extends GenericGwtRpcServiceAsync<D>>
		extends GwtRpcDataSource {
	
	private GenericGwtRpcServiceAsync<D> serviceAsync;
	
	public GenericGwtRpcDataSource() {
		super();
		setDataSourceFields();
		serviceAsync = getServiceAsync();
	}

	
	public GenericGwtRpcDataSource(String id) {
		super();
		setID(id);
		setDataSourceFields();
		serviceAsync = getServiceAsync();
	}

	/**
	 * @return a list of {@link DataSourceField}, used to define the fields of
	 *         your {@link DataSource}. NOTE: Make sure to set a primary key, as
	 *         some problems might occur if it's omitted.
	 */
	public abstract List<DataSourceField> getDataSourceFields();
	
	
	/**
	 * @return a list of fields to set in grids
	 */
	public abstract List<?> getDataSourceFieldsAsGridFields();

	/**
	 * Copies values from the {@link Record} to the data object.
	 * 
	 * @param from
	 *            the {@link Record} to copy from.
	 * @param to
	 *            the data object to copy to.
	 */
	public abstract void copyValues(R from, D to);

	/**
	 * Copies values from the data object to the {@link Record}.
	 * 
	 * @param from
	 *            the data object to copy from.
	 * @param to
	 *            the {@link Record} to copy to.
	 */
	public abstract void copyValues(D from, R to);

	/**
	 * @return the {@link GenericGwtRpcServiceAsync} to use, created
	 *         using
	 *         <code>GWT.create(YourGenericGwtRpcDataSourceService.class)</code>.
	 * 
	 *         This is unfortunately necessary as <code>GWT.create()</code> only
	 *         allows class literal as argument. We cannot create a class
	 *         literal from a parameterized type because it has no exact runtime
	 *         type representation, which is due to type erasure at compile
	 *         time.
	 */
	public abstract SA getServiceAsync();

	/**
	 * @return a new instance of your {@link Record}, such as
	 *         <code>new Record()</code> or <code>new ListGridRecord()</code>.
	 * 
	 *         This method is needed because we cannot instantiate parameterized
	 *         types at runtime. It also increases flexibility as we can pass
	 *         more complex default objects.
	 */
	public abstract R getNewRecordInstance();

	/**
	 * @return a new instance of your data object, such as
	 *         <code>new YourDataObject()</code>.
	 * 
	 *         This method is needed because we cannot instantiate parameterized
	 *         types at runtime. It also increases flexibility as we can pass
	 *         more complex default objects.
	 */
	public abstract D getNewDataObjectInstance();

	@SuppressWarnings("unchecked")
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		final Integer startRow = request.getStartRow();
		final Integer endRow = request.getEndRow();
		//put criterias in a hashmap
		Criteria criteria = request.getCriteria();
		Map<String, String> criterias = new HashMap<String, String>();
		if (criteria != null) {			
			criterias = criteria.getValues();
		}
		
		if (request.getExportResults()){
			serviceAsync.download(request.getAttribute("sortBy"),criterias,new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					SC.warn("Failed to download grid");
				}
				
				public void onSuccess(String result) {
					if (result!=null && !result.equals("")){
						Window.open(GWT.getModuleBaseURL() + "fileProvider?file="+result, "_self", "");
					}else{
						SC.warn("Can't find file on server");
					}
				}
			
			});
		}else{
			serviceAsync.fetch(
					startRow, 
					endRow, 
					// we can't use request.getSortBy() here because it throws a ClassCastException (known bug).
					//TODO: replace with request.getSortBy() as soon as the bug is fixed.
					request.getAttribute("sortBy"), 
					criterias,
					new AsyncCallback<List<D>>() {
						public void onFailure(Throwable caught) {
							response.setStatus(RPCResponse.STATUS_FAILURE);
							processResponse(requestId, response);
						}

						public void onSuccess(List<D> result) {
							List<R> records = new ArrayList<R>();
							for (D data : result) {
								R newRec = getNewRecordInstance();
								copyValues(data, newRec);
								records.add(newRec);
							}
							// if those are set, the client wants paging. you have to use GenericGwtRpcList
							if (startRow != null && endRow != null && result instanceof GenericGwtRpcList<?>) {
								Integer totalRows = ((GenericGwtRpcList<D>)result).getTotalRows();
								response.setStartRow(startRow);
								if (totalRows == null) {
									throw new NullPointerException("totalRows cannot be null when using GenericGwtRpcList");
								}
								// endRow can't be higher than totalRows
								response.setEndRow(endRow.intValue() < totalRows.intValue() ? endRow : totalRows);
								response.setTotalRows(totalRows);
							}
							response.setData(records.toArray(new Record[records.size()]));
							processResponse(requestId, response);
						}
					});
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		// Retrieve record which should be added.
		R newRec = getNewRecordInstance();
		newRec.setJsObj(request.getData());
		D data = getNewDataObjectInstance();
		copyValues(newRec, data);
		serviceAsync.add(data, new AsyncCallback<D>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}

			public void onSuccess(D result) {
				R newRec = getNewRecordInstance();
				copyValues(result, newRec);
				response.setData(new Record[] { newRec });
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeUpdate(final String requestId,
			final DSRequest request, final DSResponse response) {
		// Retrieve record which should be updated.
		R editedRec = getEditedRecord(request);
		D data = getNewDataObjectInstance();
		copyValues(editedRec, data);
		serviceAsync.update(data, new AsyncCallback<D>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}

			public void onSuccess(D result) {
				R updatedRec = getNewRecordInstance();
				copyValues(result, updatedRec);
				response.setData(new Record[] { updatedRec });
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
		// Retrieve record which should be removed.
		final R rec = getNewRecordInstance();
		rec.setJsObj(request.getData());
		D data = getNewDataObjectInstance();
		copyValues(rec, data);
		serviceAsync.remove(data, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}

			public void onSuccess(Void v) {
				// We do not receive removed record from server.
				// Return record from request.
				response.setData(new Record[] { rec });
				processResponse(requestId, response);
			}
		});
	}

	protected R getEditedRecord(DSRequest request) {
		// Creating new record for combining old values with changes
		R newRecord = getNewRecordInstance();
		// Retrieving values before edit
		if (request.getOldValues() != null) {
			JavaScriptObject oldValues = request.getOldValues().getJsObj();			
			// Copying properties from old record
			JSOHelper.apply(oldValues, newRecord.getJsObj());
		}
		// Retrieving changed values
		JavaScriptObject changedData = request.getData();
		// Apply changes
		JSOHelper.apply(changedData, newRecord.getJsObj());
		return newRecord;
	}

	private void setDataSourceFields() {
		List<DataSourceField> fields = getDataSourceFields();
		if (fields != null) {
			for (DataSourceField field : fields) {
				addField(field);
			}
		}
	}
}