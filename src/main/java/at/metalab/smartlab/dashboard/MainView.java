package at.metalab.smartlab.dashboard;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

@Route
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Div buttonOnOff(String label, //
			ComponentEventListener<ClickEvent<Button>> on, //
			ComponentEventListener<ClickEvent<Button>> off) {

		Button onBtn = new Button("ON");
		onBtn.addClickListener(on);

		Button offBtn = new Button("OFF");
		offBtn.addClickListener(off);

		Div d = new Div();
		d.add(label);
		d.add(" ");
		d.add(onBtn);
		d.add(" ");
		d.add(offBtn);

		return d;
	}

	public MainView(HomeassistantService s) {
		Tab tab1 = new Tab("Mainroom");
		Div page1 = new Div();
		page1.add(buttonOnOff("Umbrellas", //
				l -> s.haLightTurn("light.umbrellas", true), //
				l -> s.haLightTurn("light.umbrellas", false)));
		page1.add(buttonOnOff("Stage", //
				l -> s.haSwitchTurn("group.stagelights", true), //
				l -> s.haSwitchTurn("group.stagelights", false)));

		Tab tab2 = new Tab("WEL");
		Div page2 = new Div();
		page2.add(buttonOnOff("WEL Bench Left", //
				l -> s.haSwitchTurn("switch.welbenchlightleft", true), //
				l -> s.haSwitchTurn("switch.welbenchlightleft", false)));
		page2.add(buttonOnOff("WEL Bench Right", //
				l -> s.haSwitchTurn("switch.welbenchlightright", true), //
				l -> s.haSwitchTurn("switch.welbenchlightright", false)));
		page2.setVisible(false);

		Tab tab3 = new Tab("Lounge");
		Div page3 = new Div();
		page3.add(buttonOnOff("Zumtobel", //
				l -> s.haLightTurn("light.loungelights_zumtobel", true), //
				l -> s.haLightTurn("light.loungelights_zumtobel", false)));
		page3.add(buttonOnOff("Squarelamp", //
				l -> s.haSwitchTurn("switch.squarelamp", true), //
				l -> s.haSwitchTurn("switch.squarelamp", false)));
		page3.setVisible(false);

		Tab tab4 = new Tab("Entrance");
		Div page4 = new Div();
		page4.add(buttonOnOff("Ceiling", //
				l -> s.haSwitchTurn("switch.einganglicht", true), //
				l -> s.haSwitchTurn("switch.einganglicht", false)));
		page4.add(buttonOnOff("Floodlight", //
				l -> s.haSwitchTurn("switch.eingangstrahler", true), //
				l -> s.haSwitchTurn("switch.eingangstrahler", false)));
		page4.add(buttonOnOff("Blinkentunnel", //
				l -> s.haSwitchTurn("switch.blinkentunnel", true), //
				l -> s.haSwitchTurn("switch.blinkentunnel", false)));
		page4.setVisible(false);

		Tab tab5 = new Tab("Other");
		Div page5 = new Div();
		Button shutdownBtn = new Button("Shutdown");
		shutdownBtn.addClickListener(l -> s.service("homeassistant", "turn_on",
				"{ \"entity_id\" : \"automation.react_on_mqtt_shutdown_trigger\" }"));
		page5.add(shutdownBtn);

		page5.add(" ");

		Button antishutdownBtn = new Button("Startup");
		antishutdownBtn.addClickListener(
				l -> s.service("homeassistant", "turn_on", "{ \"entity_id\" : \"automation.antishutdown\" }"));
		page5.add(antishutdownBtn);
		page5.setVisible(false);

		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, page1);
		tabsToPages.put(tab2, page2);
		tabsToPages.put(tab3, page3);
		tabsToPages.put(tab4, page4);
		tabsToPages.put(tab5, page5);

		Tabs tabs = new Tabs(tab1, tab2, tab3, tab4, tab5);
		Div pages = new Div(page1, page2, page3, page4, page5);
		Set<Component> pagesShown = new HashSet<>();
		pagesShown.add(page1);

		tabs.addSelectedChangeListener(event -> {
			pagesShown.forEach(page -> page.setVisible(false));
			pagesShown.clear();
			Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
			selectedPage.setVisible(true);
			pagesShown.add(selectedPage);
		});

		HorizontalLayout actions = new HorizontalLayout(tabs);

		InputStreamFactory f = new InputStreamFactory() {

			private static final long serialVersionUID = 1L;

			@Override
			public InputStream createInputStream() {
				return Thread.currentThread().getContextClassLoader().getResourceAsStream("metalab-logo.png");
			}
		};

		Div header = new Div();
		Image i = new Image(//
				new StreamResource("metalab-logo.png", f), //
				"Metalab Logo");
		header.add(i);
		header.add(new Text("Metalab Smartlab - Dashboard"));

		add(header, actions, pages);
	}
}