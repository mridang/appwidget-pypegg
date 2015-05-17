package com.mridang.pypegg;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.mridang.widgets.BaseWidget;
import com.mridang.widgets.SavedSettings;
import com.mridang.widgets.WidgetHelpers;
import com.mridang.widgets.utils.GzippedClient;

/**
 * This class is the provider for the widget and updates the data
 */
public class LauncherWidget extends BaseWidget {

	/*
	 * @see com.mridang.widgets.BaseWidget#fetchContent(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings)
	 */
	@Override
	public String fetchContent(Context ctxContext, Integer intInstance, SavedSettings objSettings)
			throws Exception {

		final DefaultHttpClient dhcClient = GzippedClient.createClient();
		final HttpGet getProjects = new HttpGet("https://pypi.python.org/pypi/" + objSettings.get("package") + "/json");
		final HttpResponse resProjects = dhcClient.execute(getProjects);

		final Integer intResponse = resProjects.getStatusLine().getStatusCode();
		if (intResponse != HttpStatus.SC_OK) {
			throw new HttpResponseException(intResponse, "Server responded with code " + intResponse);
		}

		return EntityUtils.toString(resProjects.getEntity(), "UTF-8");

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getIcon()
	 */
	@Override
	public Integer getIcon() {

		return R.drawable.ic_notification;

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getKlass()
	 */
	@Override
	protected Class<?> getKlass() {

		return getClass();

	}

	/*
	 * @see com.mridang.BaseWidget#getToken()
	 */
	@Override
	public String getToken() {

		return "a1b2c3d4";

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#updateWidget(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings, java.lang.String)
	 */
	@Override
	public void updateWidget(Context ctxContext, Integer intInstance, SavedSettings objSettings, String strContent)
			throws Exception {

		final RemoteViews remView = new RemoteViews(ctxContext.getPackageName(), R.layout.widget);
		final JSONObject jsoData = new JSONObject(strContent);

		remView.setTextViewText(R.id.package_name, jsoData.getJSONObject("info").getString("name"));
		remView.setTextViewText(R.id.download_count, jsoData.getJSONArray("urls").getJSONObject(0).getString("downloads"));

		final String strWebpage = "http://pypi.python.org/pypi/" + objSettings.get("package");
		final PendingIntent pitOptions = WidgetHelpers.getIntent(ctxContext, WidgetSettings.class, intInstance);
		final PendingIntent pitWebpage = WidgetHelpers.getIntent(ctxContext, strWebpage);
		remView.setTextViewText(R.id.last_update, DateFormat.format("kk:mm", new Date()));
		remView.setOnClickPendingIntent(R.id.settings_button, pitOptions);
		remView.setOnClickPendingIntent(R.id.widget_icon, pitWebpage);

		AppWidgetManager.getInstance(ctxContext).updateAppWidget(intInstance, remView);

	}

}