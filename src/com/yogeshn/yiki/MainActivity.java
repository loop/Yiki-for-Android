package com.yogeshn.yiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.beanie.imagechooser.api.ChooserType;
import com.beanie.imagechooser.api.ChosenImage;
import com.beanie.imagechooser.api.ImageChooserListener;
import com.beanie.imagechooser.api.ImageChooserManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ImageChooserListener {

	EditText postTitle;
	EditText postContent;
	TextView date;
	File folder;
	File externalSdCard;
	File sdCard;
	Map<String, File> externalLocations;
	ImageChooserManager image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		postTitle = (EditText) findViewById(R.id.post_title);
		postContent = (EditText) findViewById(R.id.post_content);
		date = (TextView) findViewById(R.id.date);
		setDate();
		externalLocations = ExternalStorage.getAllStorageLocations();
		sdCard = externalLocations.get(ExternalStorage.SD_CARD);
		externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
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
		} else if (item.getItemId() == R.id.action_paragraph) {
			addParagraphTags("<p></p>");
			return true;
		} else if (item.getItemId() == R.id.action_bold) {
			addParagraphTags("<b></b>");
			return true;
		} else if (item.getItemId() == R.id.action_italic) {
			addParagraphTags("<em></em>");
			return true;
		} else if (item.getItemId() == R.id.action_image) {
			addImage();
			return true;
		}
		return false;
	}

	private void addImage() {
		image = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
		image.setImageChooserListener(this);
		image.choose();
	}

	private void addParagraphTags(String tags) {
		int start = postContent.getSelectionStart();
		int end = postContent.getSelectionEnd();

		postContent.getText().replace(Math.min(start, end), Math.max(start, end), tags, 0, tags.length());
	}

	private void clearContent() {
		postContent.setText("");
		postTitle.setText("");
		Toast.makeText(this, "Cleared!", Toast.LENGTH_SHORT).show();

	}

	private void savePost() {
		folder = new File(externalLocations.get("externalSdCard") + "/Yikis");
		if (!folder.exists()) {
			folder.mkdirs();
			createStyleSheet();
		}
		File myFile = new File(folder, postTitleFormatter() + ".html");
		if (myFile.exists()) {
			Toast.makeText(this, "File with same name exists", Toast.LENGTH_SHORT).show();
		} else {
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
				createPostIndex();
				Toast.makeText(this, "Post saved.", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Something went wrong. Post not saved.", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@SuppressLint("SimpleDateFormat")
	private void createPostIndex() {
		Date year = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String yearString = df.format(year);
		File postIndex = new File(folder, yearString + ".html");
		if (postIndex.exists()) {
			try {
				FileWriter writer = new FileWriter(postIndex, true);

				writer.append("<a href=\"" + postTitleFormatter() + ".html" + "\">" + postTitle.getText().toString()
						+ "</a> - " + "<em>" + date.getText().toString() + "</em>" + "<br >");
				writer.append(System.getProperty("line.separator"));
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				FileWriter writer = new FileWriter(postIndex, true);
				writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
				writer.append(System.getProperty("line.separator"));
				writer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\">");
				writer.append(System.getProperty("line.separator"));
				writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" >");
				writer.append(System.getProperty("line.separator"));
				writer.append("<head>\n<title>" + yearString
						+ "</title>\n<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n</head>");
				writer.append(System.getProperty("line.separator"));
				writer.append("<body>");
				writer.append(System.getProperty("line.separator"));
				writer.append("<h1>\n<a href=\"#\" class=\"p\" rel=\"nofollow\">" + yearString + "</a>\n</h1>\n");
				writer.append("<a href=\"" + postTitleFormatter() + ".html" + "\">" + postTitle.getText().toString()
						+ "</a> - " + "<em>" + date.getText().toString() + "</em>" + "<br >");
				writer.append(System.getProperty("line.separator"));
				writer.append("\n</body>\n</html>");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean noContent() {
		if (postContent.getText().toString().length() == 0 || postTitle.getText().toString().length() == 0) {
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
			writer.append("@charset \"utf-8\"; #wiki { font-family: \"Courier New\", Courier, monospace; } .p {font-family: \"Courier New\", Courier, monospace;}.p {font-family: \"Courier New\", Courier, monospace;}.h1 {font-family: \"Courier New\", Courier, monospace;}.nav {font-family: \"Courier New\", Courier, monospace;} body{font-family: \"Courier New\", Courier, monospace;}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String postTitleFormatter() {
		String sPostTitle = postTitle.getText().toString().toLowerCase(Locale.getDefault())
				.replaceAll("[^a-z\\sA-Z0-9]", "");
		String formattedPostTitle = sPostTitle.replace(" ", "-");
		return formattedPostTitle;
	}

	private void copyFile(String inputPath, String inputFile, String outputPath) {

		InputStream in = null;
		OutputStream out = null;
		try {

			// create output directory if it doesn't exist
			File dir = new File(outputPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			in = new FileInputStream(inputPath + inputFile);
			out = new FileOutputStream(outputPath + inputFile);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;

			// write the output file (You have now copied the file)
			out.flush();
			out.close();
			out = null;

		} catch (FileNotFoundException fnfe1) {
			Log.e("tag", fnfe1.getMessage());
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

	@Override
	public void onImageChosen(ChosenImage image) {
		Date year = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String yearString = df.format(year);
		File imageFolder = new File(externalLocations.get("externalSdCard") + "/Yikis/" + yearString);
		if (!imageFolder.exists()) {
			imageFolder.mkdirs();
		}
		copyFile(image.getFilePathOriginal(), image.getFilePathOriginal(), imageFolder.getAbsolutePath());
	}

	@Override
	public void onError(final String reason) {
	    runOnUiThread(new Runnable() {

	        @Override
	        public void run() {
	           Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && 
	        (requestCode == ChooserType.REQUEST_PICK_PICTURE ||
	                requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
	        image.submit(requestCode, data);
	    }
	}

}
