package me.dabpessoa.framework.enums;

public enum ViewState {
	NONE, VIEW, INSERT, UPDATE, DELETE, SEARCH;

	private ViewState() {}

	public static ViewState findByEnumName(String name) {
		if (name == null) return null;
		ViewState[] viewStates = values();
		for (ViewState viewState : viewStates) {
			if (viewState.name().equalsIgnoreCase(name)) {
				return viewState;
			}
		} return null;
	}

}
