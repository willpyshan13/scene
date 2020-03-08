/*
 * Copyright (C) 2019 ByteDance Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytedance.scene.group;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by JiangQi on 8/13/18.
 */
public abstract class ReuseGroupScene extends GroupScene {
    private ViewGroup mReuseView;
    private Context mReuseContextThemeWrapper;
    private int mReuseViewHostActivityIdentifyHashCode = -1;
    private int mReuseViewHostActivityThemeIdentifyHashCode = -1;

    @Override
    public void onAttach() {
        super.onAttach();
        if (this.mReuseViewHostActivityIdentifyHashCode == -1 && this.mReuseViewHostActivityThemeIdentifyHashCode == -1) {
            return;
        }
        if (this.mReuseViewHostActivityIdentifyHashCode != requireActivity().hashCode() ||
                this.mReuseViewHostActivityThemeIdentifyHashCode != requireActivity().getTheme().hashCode()) {
            this.mReuseView = null;
            this.mReuseContextThemeWrapper = null;
        }
    }

    @NonNull
    @Override
    public final ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
         * Can not be reused during the animation process.
         * Once the Parent is found, throw an Exception.
         */
        if (this.mReuseView != null) {
            if (this.mReuseView.getParent() != null) {
                throw new IllegalArgumentException("ReuseGroupScene reuseView already have parent");
            }
        }

        if (this.mReuseView != null) {
            return this.mReuseView;
        } else {
            return onCreateNewView(inflater, container, savedInstanceState);
        }
    }

    @Nullable
    @Override
    public Context onGetSceneContext() {
        if (this.mReuseContextThemeWrapper != null) {
            return this.mReuseContextThemeWrapper;
        }
        return super.onGetSceneContext();
    }

    @NonNull
    protected abstract ViewGroup onCreateNewView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mReuseViewHostActivityIdentifyHashCode = requireActivity().hashCode();
        this.mReuseViewHostActivityThemeIdentifyHashCode = requireActivity().getTheme().hashCode();
        this.mReuseContextThemeWrapper = requireSceneContext();
        this.mReuseView = (ViewGroup) getView();
    }
}
