package com.just.cn.mgg.ui.home;

/**
 * Retained only to guard against accidental reuse of the legacy factory.
 */
@Deprecated(forRemoval = true, since = "1.0")
public final class HomeViewModelFactory {

    private HomeViewModelFactory() {
        throw new AssertionError("HomeViewModelFactory is obsolete. Request HomeViewModel via Hilt.");
    }
}
