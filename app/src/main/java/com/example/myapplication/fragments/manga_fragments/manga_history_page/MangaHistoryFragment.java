package com.example.myapplication.fragments.manga_fragments.manga_history_page;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapters.mangaadapters.recycleradapters.MangaRecyclerHistoryAdapterNew;
import com.example.myapplication.databinding.FragmentAnimeBookmarkBinding;
import com.example.myapplication.localstorages.manga_local.read_history.MangaHistoryModel;
import com.google.gson.Gson;

import java.util.List;

import static com.example.myapplication.MyApp.localAppDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class MangaHistoryFragment extends Fragment implements SearchView.OnQueryTextListener {
    private FragmentAnimeBookmarkBinding mBinding;
    private Context mContext;

    public MangaHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAnimeBookmarkBinding.inflate(inflater, container, false);
        getDataFromLocalDB("ordinary", "");
        initUI();
        initEvent();
        return mBinding.getRoot();
    }

    private void initUI() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initEvent() {
        mBinding.swipeRefreshAnimeBookmark.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeRefreshAnimeBookmark.setRefreshing(false);
                getDataFromLocalDB("ordinary", "");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromLocalDB("ordinary", "");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchBar));
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getDataFromLocalDB(String hitStatus, String newText) {
        List<MangaHistoryModel> historyModelList;
        if (hitStatus.equalsIgnoreCase("ordinary")) {
            historyModelList = localAppDB.mangaHistoryDAO().getMangaHistoryData();
            Log.e("read history", new Gson().toJson(historyModelList));
            if (validateList(historyModelList)) {
                showRecyclerResult(historyModelList);
            } else {
                showErrorLayout("Oops, you haven't marked your favourite manga");
            }
        } else {
            historyModelList = localAppDB.mangaHistoryDAO().searchByName("%" + newText + "%");
            if (validateList(historyModelList)) {
                showRecyclerResult(historyModelList);
            } else {
                showErrorLayout("Oops, please type correctly");
            }
        }
    }

    private boolean validateList(List<MangaHistoryModel> historyModelList) {
        return historyModelList != null && historyModelList.size() > 0;
    }

    private void showRecyclerResult(List<MangaHistoryModel> historyModelList) {
        mBinding.recylerAnimeBookmark.setVisibility(View.VISIBLE);
        mBinding.linearError.setVisibility(View.GONE);
        mBinding.recylerAnimeBookmark.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.recylerAnimeBookmark.setAdapter(new MangaRecyclerHistoryAdapterNew(getActivity(), historyModelList));
        mBinding.recylerAnimeBookmark.setHasFixedSize(true);
    }

    private void showErrorLayout(String errorMessage) {
        mBinding.recylerAnimeBookmark.setVisibility(View.GONE);
        Glide.with(mContext).asGif().load(R.raw.aquacry).into(mBinding.imageError);
        mBinding.textViewErrorMessage.setText(errorMessage);
        mBinding.linearError.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getDataFromLocalDB("search", query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getDataFromLocalDB("search", newText);
        return true;
    }
}
