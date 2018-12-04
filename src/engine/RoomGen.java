package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import engine.things.Entity;
import engine.things.Object;
import engine.things.Player;

public abstract class RoomGen {
	public static Room gen(Room map, ArrayList<Object> objectQueue) {
		Room mainArea = new Room(0, 0, "Colossal Cave");
		map.addRoom(mainArea);
		
		Room start = new Room(0, 0, "A Dark Cavern");
		mainArea.addRoom(start);
		
		Object o = new Object("red [brick]", "on a", null);
		o.injury = Object.type.shatters;
		Object reference = new Object("nice hand-knitted [carpet]", o, null);
		o.reference = reference;
		start.objects.add(o);
		
		/*o = new Object("deformed [spider]", "on your", null);
		o.injury = Object.type.squishes;
		reference = new Object("[face]", o, null);
		reference.abstractNoun();
		o.reference = reference;
		start.objects.add(o);
		
		o = Engine.Consumable("dead [corpse]", "lying on", null, 10);
		o.injury = Object.type.bruises;
		o.holdable = null;
		reference = map.nestedMap.get(0).floor;
		o.reference = reference;
		start.objects.add(o);
		
		o = new Object("old wooden [bookshelf]", "in", null);
		o.injury = Object.type.shatters;
		o.holdable = null;
		o.container.addAll(Arrays.asList(new Object("dusty old [book]", o, null),
				new Object("trigonometry [textbook]", o, null), new Object("[jar] full of candy", o, null)));
		reference = new Object("the back of the [room]", o, null);
		reference.abstractNoun();
		o.reference = reference;
		start.objects.add(o);*/
		
		Entity e = new Entity("an old [man]", "standing in front of", 
				(Player p, Engine e1) -> {
					Terminal.println("The old man says hi.");
					}, 
				(Engine e2) -> {
					Terminal.println("The old man dies. He leaves you a corpse as a parting gift.");
					Object obj = Engine.Consumable("dead [corpse]", "lying on", null, 10);
					obj.injury = Object.type.bruises;
					obj.holdable = null;
					Object ref = new Object("the [floor]", obj, null);
					ref.abstractNoun();
					obj.reference = ref;
					objectQueue.add(obj);
					});
		reference = new Object("[you]", o, null);
		reference.abstractNoun();
		e.reference = reference;
		start.objects.add(e);
		
		Room r = new Room(0, 1, "A Dark Stone Passageway");
		mainArea.addRoom(r);
		
		o = new Object("chunk of [obsidian]", "in a", null);
		o.injury = Object.type.shatters;
		reference = new Object("small [puddle]", o, null);
		o.reference = reference;
		r.objects.add(o);
	
		mainArea.setEntries();//to be called after mainArea completely defined
		//alternatively, you could pick the entry points directly if you want more control
		
		mainArea = new Room(0, 1, "Emerald Forest");
		map.addRoom(mainArea);
		
		mainArea.addRoom(new Room(0, 0, "A Small Grove"));
		
		mainArea.setEntries();
		
		
		ArrayList<Object> references = new ArrayList<Object>();
		compileReferences(map, references);
		addToRooms(map, references);
		
		return start;
	}
	
	public static void compileReferences(Room map, ArrayList<Object> references) {
		for (Room r : map.nestedMap) {
			Iterator<Object> it = r.objects.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj.reference != null) {
					references.add(obj.reference);
				}
				if (!obj.container.isEmpty()) {
					references.addAll(obj.container);
				}
			}
			if (r.nestedMap.size() > 0)
				compileReferences(r, references);
		}
	}
	
	public static void addToRooms(Room map, ArrayList<Object> aL) {
		for (Room r : map.nestedMap) {
			r.objects.addAll(aL);
			if (r.nestedMap.size() > 0)
				addToRooms(r, aL);
		}
	}
}