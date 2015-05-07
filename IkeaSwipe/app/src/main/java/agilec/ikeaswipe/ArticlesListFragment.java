package agilec.ikeaswipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * List containing all the IKEA items. Used in the ListView.
 */
public class ArticlesListFragment extends ListFragment {

  AllArticles articleHandler = null;
  ListAdapter ourAdapter = null;
  private int currentStep = 0;

  /**
   * Creates a list view  with all the items added to the list
   *
   * @param inflater           too fill the layout with content
   * @param container
   * @param savedInstanceState
   * @return The list view with all the items added to the list
   * @author @martingrad @marcusnygren @LinneaMalcherek
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ListView listView = new ListView(getActivity()); // Create a list view in the current activity
    listView.setId(android.R.id.list); // Sets ID according to the Android documentation

    // Get all articles for Kritter
    try {
      articleHandler = new AllArticles("kritter_parts.json", getActivity());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Intent intent = getActivity().getIntent();

    String articleImgUrl = intent.getStringExtra("objectFound");
    if (articleImgUrl != null) {
      articleHandler.updateCheckedFromArActivity(articleImgUrl); // set the article to true
      articleHandler.updateAndSaveJson(getActivity()); // notify the database of the change
    }

    // Set our adapter with the current step
    ArrayList<Article> currentList = new ArrayList(articleHandler.getArticlesInStep(currentStep));
    ourAdapter = new ListAdapter(getActivity(), R.layout.list_item, currentList);

    /**
     *  Connects the items to the list view activity, using the layout specified in the second parameter
     *  Third parameter = an ArrayList with all our articles
     */
    setListAdapter(ourAdapter);
    return listView;
  }


  /**
   * OnClick on a list item
   *
   * @param l        ListView
   * @param v        View
   * @param position int
   * @param id       long
   * @author @jacobselg
   */
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Article article = (Article) getListAdapter().getItem(position);
    System.out.println(article.getTitle());

    Intent arIntent = new Intent(getActivity(), ArFindAllActivity.class);
    arIntent.putExtra("article", article.getImgUrl());
    startActivity(arIntent);
  }

  /**
   * Updates out list depending on which step is active
   *
   * @param step Current step in the application
   * @throws JSONException
   * @user @ingelhag
   */
  public void updateListWithStep(int step) throws JSONException {

    // Set current step - needed to be done to set correct quantity in the list
    // Set all articles that will be shown in the list depending on the current step
    currentStep = step;
    List<Article> theList = articleHandler.getArticlesInStep(step);

    /**
     * Notify the list something will be changed
     * Delete all data from the list
     */
    ourAdapter.clear();
    ourAdapter.addAll(theList);
    ourAdapter.notifyDataSetChanged();
  }

  /**
   * A custom ArrayAdapter, used to achieve the desirable list style with an image alongside text.
   *
   * @user @martingrad @jacobselg @ingelhag
   */
  private class ListAdapter extends ArrayAdapter<Article> {
    //Copy of the arrayList of Articles
    private ArrayList<Article> article;

    //Constructor, copying passed ListItems to items.
    public ListAdapter(Context context, int textViewResourceId, ArrayList<Article> article) {
      super(context, textViewResourceId, article);
      this.article = article;
    }


    /* Display a single article
       * @param position The position of the item within the list
       * @param convertView The old view to reuse, if possible.
       * @param parent The list view containing all the articles, see docs for ArrayAdapter
       * @return view for a single article
       * @user @ingelhag
       */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.list_item, null);
      }

      // Get one article and print it into a view
      final Article a = article.get(position);
      if (a != null) {
        //Finding the current ListItems TopText, BottomText and Image
        TextView listTopText = (TextView) v.findViewById(R.id.toptext);
        TextView listBottomText = (TextView) v.findViewById(R.id.bottomtext);
        ImageView listImg = (ImageView) v.findViewById(R.id.icon);
        ImageButton arButton = (ImageButton) v.findViewById(R.id.arButton);
        Button statusButton = (Button) v.findViewById(R.id.status);

        if (a.getChecked()) {
          statusButton.setBackgroundColor(Color.GREEN);
        }

        /**
         * If user click on Ar Button:
         * 1. Creates an intent
         * 2. Send a string with imgUrl of the article
         * 3. Start activity
         */
        arButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent arIntent = new Intent(getActivity(), ArFindAllActivity.class);
            arIntent.putExtra("article", a.getImgUrl());
            startActivity(arIntent);
          }
        });

        // Set title
        listTopText.setText(a.getTitle());

        /**
         * Set quantity
         * If step != 0 - set quantity for each step
         */
        if (currentStep == 0) {
          listBottomText.setText(a.getQuantity() + "x");
        } else {
          listBottomText.setText(a.getSteps()[currentStep - 1] + "x");
        }

        // Set image
        int id = getResources().getIdentifier(a.getImgUrl(), "drawable", getActivity().getPackageName());
        listImg.setImageResource(id);
      }
      // Return the view
      return v;
    }
  }
}