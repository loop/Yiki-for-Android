package com.yogeshn.yiki;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText postTitle;
	EditText postContent;
	TextView date;
	File folder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		postTitle = (EditText) findViewById(R.id.post_title);
		postContent = (EditText) findViewById(R.id.post_content);
		date = (TextView) findViewById(R.id.date);
		setDate();

	}

	@SuppressLint("SimpleDateFormat")
	private void setDate() {
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-LLLLLLLLL-yyyy");
		date.setText(df.format(today));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_save) {
			if (!noContent()) {
			savePost();
			}
			return true;
		} else if (item.getItemId() == R.id.menu_clear) {
			clearContent();
			return true;
		}
		return false;
	}

	private void clearContent() {
		postContent.setText("");
		postTitle.setText("");
		Toast.makeText(this, "Cleared!", Toast.LENGTH_SHORT).show();

	}

	private void savePost() {
		folder = new File(Environment.getExternalStorageDirectory().toString() + "/yiki");
		if (!folder.exists()) {
			folder.mkdirs();
			createStyleSheet();
		}
		File myFile = new File(folder, postTitleFormatter() + ".html");
		try {
			FileWriter writer = new FileWriter(myFile);
			writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			writer.append(System.getProperty("line.separator"));
			writer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\">");
			writer.append(System.getProperty("line.separator"));
			writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" >");
			writer.append(System.getProperty("line.separator"));
			writer.append("<head>\n<title>" + postTitle.getText().toString()
					+ "</title>\n<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n</head>");
			writer.append(System.getProperty("line.separator"));
			writer.append("<body>");
			writer.append(System.getProperty("line.separator"));
			writer.append("<h1>\n<a href=\"#\" class=\"p\" rel=\"nofollow\">" + postTitle.getText().toString()
					+ "</a>\n</h1>\n");
			writer.append("<p><em>" + date.getText().toString() + "</em></p>\n");
			writer.append("<div id=\"wiki\">\n<div>\n");
			writer.append("<p>" + postContent.getText().toString() + "</p>\n");
			writer.append("</div>\n");
			writer.append("</div>\n");
			writer.append("\n</body>\n</html>");
			writer.flush();
			writer.close();
			Toast.makeText(this, "Post saved.", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Something went wrong. Post not saved.", Toast.LENGTH_SHORT).show();
		}

	}

	private boolean noContent() {
		if (postContent.getText().toString().length()==0 || postTitle.getText().toString().length()==0) {
			Toast.makeText(this, "Title or content is blank", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return false;
		}
	}

	private void createStyleSheet() {
		File stylesheet = new File(folder, "style.css");
		try {
			FileWriter writer = new FileWriter(stylesheet);
			writer.append("@charset \"utf-8\"; #wiki { font-family: \"Courier New\", Courier, monospace; } .p {font-family: \"Courier New\", Courier, monospace;}.p {font-family: \"Courier New\", Courier, monospace;}.h1 {font-family: \"Courier New\", Courier, monospace;}.nav {font-family: \"Courier New\", Courier, monospace;}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String postTitleFormatter() {
		String sPostTitle = postTitle.getText().toString().toLowerCase(Locale.getDefault())
				.replaceAll("[^a-z\\sA-Z]", "");
		String formattedPostTitle = sPostTitle.replace(" ", "-");
		return formattedPostTitle;
	}

}
