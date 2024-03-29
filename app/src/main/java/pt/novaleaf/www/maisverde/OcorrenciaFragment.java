package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a listOcorrencias of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OcorrenciaFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public static RecyclerView myRecyclerView;
    private static Context mContext;
    public static List<Ocorrencia> listOcorrencias =  new ArrayList<>();
    public static MyOcorrenciaRecyclerViewAdapter myOcorrenciaRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OcorrenciaFragment() {
    }

    // TODO: Customize parameter initialization

    public static OcorrenciaFragment newInstance(int columnCount, Context context) {
        OcorrenciaFragment fragment = new OcorrenciaFragment();
        updateList();
        mContext = context;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocorrencia_list, container, false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }/**
            myOcorrenciaRecyclerViewAdapter =
                    new MyOcorrenciaRecyclerViewAdapter(listOcorrencias, mListener);
            myRecyclerView.setAdapter(myOcorrenciaRecyclerViewAdapter);*/

            OcorrenciaFragment.myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1)) {
                        Toast.makeText(mContext,"YA BINA",Toast.LENGTH_LONG).show();


                    }
                }
            });

        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public static void updateList(){
        //listOcorrencias.add(new Ocorrencia("FOGO", R.mipmap.ic_entrada_round, 5, "11:45\n13/05/2018"));
        //listOcorrencias.add(new Ocorrencia("ajuda", R.mipmap.ic_logo_round, 3, "18:13\n16/05/2018"));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener extends Serializable {
        // TODO: Update argument type and name
        void onLikeInteraction(Ocorrencia item);

        void onCommentInteraction(Ocorrencia item);

        void onEditInteraction(Ocorrencia item, View view);

        void onImagemInteraction(Ocorrencia item);
    }

}
