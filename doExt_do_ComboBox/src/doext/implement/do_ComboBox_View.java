package doext.implement;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIModuleTypeID;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import doext.define.do_ComboBox_IMethod;
import doext.define.do_ComboBox_MAbstract;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,do_ComboBox_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
public class do_ComboBox_View extends Spinner implements DoIUIModuleView, do_ComboBox_IMethod, DoIModuleTypeID, android.widget.AdapterView.OnItemSelectedListener {

	private ArrayAdapter<String> mAdapter;
	private String fontStyle;
	private String fontColor;
	private String fontSize;

	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_ComboBox_MAbstract model;

	public do_ComboBox_View(Context context) {
		super(context);
	}

	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */
	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_ComboBox_MAbstract) _doUIModule;
		this.setOnItemSelectedListener(this);
	}

	/**
	 * 动态修改属性值时会被调用，方法返回值为true表示赋值有效，并执行onPropertiesChanged，否则不进行赋值；
	 * 
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public boolean onPropertiesChanging(Map<String, String> _changedValues) {
		return true;
	}

	/**
	 * 属性赋值成功后被调用，可以根据组件定义相关属性值修改UIView可视化操作；
	 * 
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public void onPropertiesChanged(Map<String, String> _changedValues) {
		DoUIModuleHelper.handleBasicViewProperChanged(this.model, _changedValues);

		if (_changedValues.containsKey("fontStyle")) {
			this.fontStyle = _changedValues.get("fontStyle");
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}

		if (_changedValues.containsKey("fontColor")) {
			this.fontColor = _changedValues.get("fontColor");
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}

		if (_changedValues.containsKey("fontSize")) {
			this.fontSize = _changedValues.get("fontSize");
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}

		if (_changedValues.containsKey("items")) {
			String _items = _changedValues.get("items");
			String[] _data = _items.split(",");
			if (mAdapter == null) {
				mAdapter = new MyAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, _data);
				this.setAdapter(mAdapter);
			}

			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}

		if (_changedValues.containsKey("index")) {
			int _position = DoTextHelper.strToInt(_changedValues.get("index"), 0);
			if (_position < 0) {
				_position = 0;
			}
			if (mAdapter != null && _position > mAdapter.getCount()) {
				_position = mAdapter.getCount() - 1;
			}
			this.setSelection(_position);
		}

	}

	private class MyAdapter extends ArrayAdapter<String> {
		public MyAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView _tv = (TextView) super.getView(position, convertView, parent);
			setTextViewStyle(_tv);
			return _tv;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView _tv = (TextView) super.getDropDownView(position, convertView, parent);
			setTextViewStyle(_tv);
			return _tv;
		}
	}

	private void setTextViewStyle(TextView _tv) {
		DoUIModuleHelper.setFontStyle(_tv, fontStyle);
		_tv.setTextColor(DoUIModuleHelper.getColorFromString(fontColor, Color.BLACK));
		_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, DoUIModuleHelper.getDeviceFontSize(model, fontSize));
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// ...do something
		return false;
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		// ...do something
		return false;
	}

	/**
	 * 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	 */
	@Override
	public void onDispose() {
		// ...do something
	}

	/**
	 * 重绘组件，构造组件时由系统框架自动调用；
	 * 或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	 */
	@Override
	public void onRedraw() {
		this.setLayoutParams(DoUIModuleHelper.getLayoutParams(this.model));
	}

	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}

	@Override
	public String getTypeID() {
		return this.model.getTypeID();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		this.model.setPropertyValue("index", position + "");
		DoInvokeResult _result = new DoInvokeResult(this.model.getUniqueKey());
		_result.setResultInteger(position);
		this.model.getEventCenter().fireEvent("selectChanged", _result);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}