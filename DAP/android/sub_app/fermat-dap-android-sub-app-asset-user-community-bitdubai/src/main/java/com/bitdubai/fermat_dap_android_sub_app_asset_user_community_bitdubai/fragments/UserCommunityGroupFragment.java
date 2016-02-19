package com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantGetSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantPersistSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.SettingsNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.R;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.adapters.GroupCommunityAdapter;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.adapters.UserCommunityAdapter;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.dialogs.ConfirmDeleteDialog;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.interfaces.AdapterChangeListener;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.interfaces.PopupMenu;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.models.Actor;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.models.Group;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.popup.CreateGroupFragmentDialog;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.sessions.AssetUserCommunitySubAppSession;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.sessions.SessionConstantsAssetUserCommunity;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.AssetUserGroupMemberRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantDeleteAssetUserGroupException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserGroup;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserGroupMember;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.exceptions.CantPublishAssetFactoy;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.interfaces.AssetFactory;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.AssetUserSettings;
import com.bitdubai.fermat_dap_api.layer.dap_sub_app_module.asset_user_community.interfaces.AssetUserCommunitySubAppModuleManager;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Created by Nerio on 06/01/16.
 */
public class UserCommunityGroupFragment extends AbstractFermatFragment implements
        SwipeRefreshLayout.OnRefreshListener, android.widget.PopupMenu.OnMenuItemClickListener {

    private static AssetUserCommunitySubAppModuleManager manager;
    private static final int MAX = 20;

    private List<Group> groups;
    ErrorManager errorManager;

    // recycler
    private final String TAG = "DapMain";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private GroupCommunityAdapter adapter;
    private View rootView;
    private LinearLayout emptyView;
    private int offset = 0;
    private CreateGroupFragmentDialog dialog;
    private Group selectedGroup;

    SettingsManager<AssetUserSettings> settingsManager;
    /**
     * Flags
     */
    private boolean isRefreshing = false;

    public static UserCommunityGroupFragment newInstance() {
        return new UserCommunityGroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            manager = ((AssetUserCommunitySubAppSession) appSession).getModuleManager();
            errorManager = appSession.getErrorManager();
            settingsManager = appSession.getModuleManager().getSettingsManager();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.group_fragment, container, false);
//        initViews(rootView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gridViewGroup);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupCommunityAdapter(getActivity());
        adapter.setAdapterChangeListener(new AdapterChangeListener<Group>() {
            @Override
            public void onDataSetChanged(List<Group> dataSet) {
                groups = dataSet;
            }
        });
        adapter.setMenuItemClick(new PopupMenu() {
            @Override
            public void onMenuItemClickListener(View menuView, Group group, int position) {
                selectedGroup = group;
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getActivity(), menuView);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.dap_community_user_group_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(UserCommunityGroupFragment.this);
                popupMenu.show();
            }
        });

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_group);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLUE);

        rootView.setBackgroundColor(Color.parseColor("#000b12"));
        emptyView = (LinearLayout) rootView.findViewById(R.id.empty_view_group);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();


        ActionButton create = (ActionButton) rootView.findViewById(R.id.create_group);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* create new asset factory project */
//                selectedAsset = null;
//                changeActivity(Activities.DAP_ASSET_EDITOR_ACTIVITY.getCode(), appSession.getAppPublicKey(), getAssetForEdit());
                lauchCreateGroupDialog();
            }
        });
        create.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_jump_from_down));
        create.setVisibility(View.VISIBLE);

        //initialize settings
        settingsManager = appSession.getModuleManager().getSettingsManager();
        AssetUserSettings settings = null;
        try {
            settings = settingsManager.loadAndGetSettings(appSession.getAppPublicKey());
        } catch (Exception e) {
            settings = null;
        }
        if (settings == null) {
            settings = new AssetUserSettings();
            settings.setIsContactsHelpEnabled(true);
            settings.setIsPresentationHelpEnabled(true);

            try {
                settingsManager.persistSettings(appSession.getAppPublicKey(), settings);
            } catch (CantPersistSettingsException e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_GROUP, 0, "help").setIcon(R.drawable.dap_community_user_help_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        selectedAsset = null;
    }

    protected void initViews(View layout) {
        Log.i(TAG, "recycler view setup");
        if (layout == null)
            return;
        recyclerView = (RecyclerView) layout.findViewById(R.id.gridViewGroup);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new GroupCommunityAdapter(getActivity());
            adapter.setAdapterChangeListener(new AdapterChangeListener<Group>() {
                @Override
                public void onDataSetChanged(List<Group> dataSet) {
                    groups = dataSet;
                }
            });

         /*   adapter.setMenuItemClick(new PopupMenu() {
                @Override
                public void onMenuItemClickListener(View menuView, Group group, int position) {
                    selectedGroup = group;
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getActivity(), menuView);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.dap_community_user_group_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(UserCommunityGroupFragment.this);
                    popupMenu.show();
                }
            });*/
//            adapter = new AssetFactoryAdapter(getActivity());
//            adapter.setMenuItemClick(new PopupMenu() {
//                @Override
//                public void onMenuItemClickListener(View menuView, AssetFactory project, int position) {
//                    selectedAsset = project;
//                    /*Showing up popup menu*/
//                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getActivity(), menuView);
//                    MenuInflater inflater = popupMenu.getMenuInflater();
//                    inflater.inflate(R.menu.asset_factory_main, popupMenu.getMenu());
//                    try {
//                        if (!manager.isReadyToPublish(selectedAsset.getAssetPublicKey())) {
//                            popupMenu.getMenu().findItem(R.id.action_publish).setVisible(false);
//                        }
//                    } catch (CantPublishAssetFactoy cantPublishAssetFactoy) {
//                        cantPublishAssetFactoy.printStackTrace();
//                        popupMenu.getMenu().findItem(R.id.action_publish).setVisible(false);
//                    }
//                    popupMenu.setOnMenuItemClickListener(EditableAssetsFragment.this);
//                    popupMenu.show();
//                }
//            });
            recyclerView.setAdapter(adapter);

        }
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_group);
        if (swipeRefreshLayout != null) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        // fab action button create
        ActionButton create = (ActionButton) layout.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* create new asset factory project */
//                selectedAsset = null;
//                changeActivity(Activities.DAP_ASSET_EDITOR_ACTIVITY.getCode(), appSession.getAppPublicKey(), getAssetForEdit());
                lauchCreateGroupDialog();
            }
        });
        create.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_jump_from_down));
        create.setVisibility(View.VISIBLE);
    }

    private void setUpPresentation(boolean checkButton) {
        //        try {
        PresentationDialog presentationDialog = new PresentationDialog.Builder(getActivity(), appSession)
                .setBannerRes(R.drawable.banner_asset_user)
                .setIconRes(R.drawable.asset_user_comunity)
                .setVIewColor(R.color.dap_community_user_view_color)
                .setTitleTextColor(R.color.dap_community_user_view_color)
                .setSubTitle(R.string.dap_user_community_group_subTitle)
                .setBody(R.string.dap_user_community_group_body)
                .setTemplateType(PresentationDialog.TemplateType.TYPE_PRESENTATION_WITHOUT_IDENTITIES)
                .setIsCheckEnabled(checkButton)
                .build();

//            presentationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    Object o = appSession.getData(SessionConstantsAssetIssuer.PRESENTATION_IDENTITY_CREATED);
//                    if (o != null) {
//                        if ((Boolean) (o)) {
//                            //invalidate();
//                            appSession.removeData(SessionConstantsAssetIssuer.PRESENTATION_IDENTITY_CREATED);
//                        }
//                    }
//                    try {
//                        IdentityAssetIssuer identityAssetIssuer = moduleManager.getActiveAssetIssuerIdentity();
//                        if (identityAssetIssuer == null) {
//                            getActivity().onBackPressed();
//                        } else {
//                            invalidate();
//                        }
//                    } catch (CantGetIdentityAssetIssuerException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        presentationDialog.show();
//        } catch (CantGetIdentityAssetIssuerException e) {
//            e.printStackTrace();
//        }
    }

    public void showEmpty(boolean show, View emptyView) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(),
                show ? android.R.anim.fade_in : android.R.anim.fade_out);
        if (show &&
                (emptyView.getVisibility() == View.GONE || emptyView.getVisibility() == View.INVISIBLE)) {
            emptyView.setAnimation(anim);
            emptyView.setVisibility(View.VISIBLE);
            if (adapter != null)
                adapter.changeDataSet(null);
        } else if (!show && emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setAnimation(anim);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        try {
            if (id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_GROUP) {
                setUpPresentation(settingsManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                return true;
            }
        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), "Asset User system error",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void lauchCreateGroupDialog(){
        dialog = new CreateGroupFragmentDialog(
                getActivity(),manager,selectedGroup);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onRefresh();
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(true);
            FermatWorker worker = new FermatWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    return getMoreData();
                }
            };
            worker.setContext(getActivity());
            worker.setCallBack(new FermatWorkerCallBack() {
                @SuppressWarnings("unchecked")
                @Override
                public void onPostExecute(Object... result) {
                    isRefreshing = false;
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (result != null &&
                            result.length > 0) {
                        if (getActivity() != null && adapter != null) {
                            groups = (ArrayList<Group>) result[0];
                            adapter.changeDataSet(groups);
                            if (groups.isEmpty()) {
                                showEmpty(true, emptyView);
                            } else {
                                showEmpty(false, emptyView);
                            }
                        }
                    } else
                        showEmpty(true, emptyView);
                }

                @Override
                public void onErrorOccurred(Exception ex) {
                    isRefreshing = false;
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            });
            worker.execute();
        }
    }

    private synchronized List<Group> getMoreData() throws Exception {
        List<Group> dataSet = new ArrayList<>();
        List<ActorAssetUserGroup> result = null;
        if (manager == null)
            throw new NullPointerException("AssetUserCommunitySubAppModuleManager is null");
        result = manager.getGroups();
        if (result != null && result.size() > 0) {
            for (ActorAssetUserGroup record : result) {
                Group group = new Group(record);
                group.setMembers(manager.getListActorAssetUserByGroups(group.getGroupId()).size());
                dataSet.add(group);
            }
        }
        return dataSet;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            lauchCreateGroupDialog();
        }
        else if (item.getItemId() == R.id.action_delete)
        {
            appSession.setData("group_ID", selectedGroup.getGroupId());
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(getActivity(), (AssetUserCommunitySubAppSession) appSession, appResourcesProviderManager);
            dialog.setYesBtnListener(new ConfirmDeleteDialog.OnClickAcceptListener() {
                @Override
                public void onClick() {
                    String groupSelectedID;
                    try {
                        groupSelectedID = (String) appSession.getData("group_ID");
                        List<ActorAssetUser> userList = manager.getListActorAssetUserByGroups(groupSelectedID);
                        for (ActorAssetUser user : userList){

                            AssetUserGroupMemberRecord actorGroup = new AssetUserGroupMemberRecord();
                            actorGroup.setGroupId(groupSelectedID);
                            actorGroup.setActorPublicKey(user.getActorPublicKey());
                            manager.removeActorAssetUserFromGroup(actorGroup);

                        }

                        manager.deleteGroup(groupSelectedID);
                        Toast.makeText(getActivity(), "Group deleted.", Toast.LENGTH_SHORT).show();
                        onRefresh();
                    } catch (CantDeleteAssetUserGroupException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "This group couldn't be deleted.", Toast.LENGTH_SHORT).show();
                    } catch (RecordsNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Group not found.", Toast.LENGTH_SHORT).show();
                    }catch (CantGetAssetUserActorsException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Can't get users from group.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();

            //Toast.makeText(getActivity(), "Group deleted.", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.action_group_members)
        {
            appSession.setData("group_selected", selectedGroup);
            changeActivity(Activities.DAP_ASSET_USER_COMMUNITY_ACTIVITY_ADMINISTRATIVE_GROUP_USERS_FRAGMENT, appSession.getAppPublicKey());
        }

        selectedGroup = null;
        onRefresh();
        return false;
    }
}