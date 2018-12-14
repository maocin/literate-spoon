
package engine;

import java.util.*;

import engine.things.Player;
import engine.things.Effect;
import engine.things.Entity;
import engine.things.Object;
import engine.words.Verb;
import engine.words.Word;

import engine.Terminal;

public class Engine {
	public Player protag;

	public Entity antag;

	// public ArrayList<Room> rooms;// can be accessed by verbs
	public Room worldMap;
	public final String worldName = "Azeroth";// just a random name (thank Liam)

	private ArrayList<Word> vocabulary;
	private ArrayList<String> prepositions;
	private ArrayList<String> omitWords;
	public ArrayList<Object> objectQueue = new ArrayList<Object>();
	Random rand = new Random();
	
	public boolean changedSurroundings = true;

	public Engine() {
		protag = new Player(0, 0);
		protag.setHealth(100);

		antag = new Entity("The omnipotent [aidan]", "everywhere and nowhere", (Engine e2) -> {
			Terminal.println("What have you done!??!?");
			Object obj = Engine.Consumable("[tear] in space-time", "floating in", null, 10);
			obj.injury = Object.type.shatters;
			obj.holdable = null;
			Object ref = new Object("the [air]", obj, null);
			ref.abstractNoun();
			obj.reference = ref;
		}); //Not added for a reason

		// rooms = new ArrayList<Room>();
		worldMap = new Room(0, 0, "The World of " + worldName);

		protag.currentRoom = RoomGen.gen(worldMap, objectQueue);// returns starting room

		vocabulary = new ArrayList<Word>();
		prepositions = new ArrayList<String>(
				Arrays.asList(new String[] { "aboard", "about", "above", "across", "after", "against", "along", "amid",
						"among", "around", "as", "at", "before", "behind", "below", "beside", "between", "by", "down",
						"in", "inside", "into", "my", "near", "on", "the", "through", "to", "toward", "towards", "under", "with", "your"}));
		omitWords = new ArrayList<String>(
				Arrays.asList(new String[] {"up", "down"}));
		
		
	}

	public void addWord(Word v) {
		vocabulary.add(v);
	}

	public static Object Consumable(String accessor, String descriptor, String inspection, int consumability) {
		Object o = new Object(accessor, descriptor, inspection);
		o.consumability = consumability;
		return o;
	}

	public String uRandOf(String[] s) {
		int x = rand.nextInt(s.length);

		// Convert 'a's to 'an's
		for (int i = 2; i < s[x].length(); i++) {
			ArrayList<Character> vowels = new ArrayList<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
			if (vowels.contains(s[x].charAt(i)) && s[x].charAt(i - 2) == 'a' && s[x].charAt(i - 1) == ' '
					&& s[x].charAt(i - 3) == ' ') {
				s[x] = s[x].substring(0, i - 2) + "an" + s[x].substring(i - 1, s[x].length());
			}
		}
		return s[x].substring(0, 1).toUpperCase() + s[x].substring(1, s[x].length());
	}

	public String lRandOf(String[] s) {
		int x = rand.nextInt(s.length);
		// Convert 'a's to 'an's
		for (int i = 2; i < s[x].length(); i++) {
			ArrayList<Character> vowels = new ArrayList<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
			if (vowels.contains(s[x].charAt(i)) && s[x].charAt(i - 2) == 'a' && s[x].charAt(i - 1) == ' '
					&& s[x].charAt(i - 3) == ' ') {
				s[x] = s[x].substring(0, i - 2) + "an" + s[x].substring(i - 1, s[x].length());
			}
		}
		return s[x].toLowerCase();
	}

	public void updatePlayerState() {
		if (protag.hunger++ > 50) {
			if (protag.hunger > 80) {
				Terminal.println(
						"Intuition tells you that if you had eaten something, perhaps you wouldn't be so hungry.");
			} else {
				Terminal.println("Intuition tells you that you might want to eat some food.");
			}
		}
		if (protag.thirst++ > 50) {
			if (protag.thirst > 80) {
				Terminal.println("Intuition tells you that your thirst cannot be quenched by drinking air.");
			} else {
				Terminal.println("Intuition tells you that you might want to drink something.");
			}
		}
		if (protag.thirst < 20 && protag.hunger < 20) {
			protag.health += protag.health < protag.maxHealth && protag.health > 0 ? 1 : 0;
		}
	}
	
	public void inspectRoom() {
		if (protag.currentRoom != null) {
			Room holder = protag.currentRoom;
			String desc = holder.description;
			while (holder.fatherRoom != null) {
				holder = holder.fatherRoom;
				desc = holder.description + ": " + desc;
			}

			Terminal.println(desc);
		} else
			Terminal.println("Currently not in any room!");

		int x1 = 0;
		int x2 = 0;

		for (int i = 0; i < protag.currentRoom.objects.size(); i++) {
			Object o = protag.currentRoom.objects.get(i);
			String compSub = o.compSub;
			if (o.health != null && o.health < o.maxHealth) {
				int p = (int) (((float) o.health / (float) o.maxHealth) * 4);
				switch (o.injury) {
				case crumples:
					compSub = (p == 3 ? "dented " : p == 2 ? "bent " : p == 1 ? "crumpled-up " : "crushed ")
							+ o.compSub;
					break;
				case shatters:
					compSub = (p == 3 ? "fractured " : p > 0 ? "cracked " : "shattered ") + o.compSub;
					break;
				case squishes:
					compSub = (p == 3 ? "bruised " : p == 2 ? "squashed " : p == 1 ? "compressed " : "trampled ")
							+ o.compSub;
					break;
				case bruises:
					compSub = (p == 3 ? "bruised " : p == 2 ? "damaged " : p == 1 ? "beaten-up " : "pulverized ")
							+ o.compSub;
					break;
				}
			}
			String rCompSub = "";
			if (o.reference != null) {
				rCompSub = o.reference.compSub;
				if (o.reference.health != null && o.reference.health < o.reference.maxHealth
						&& o.reference.injury != null) {
					int p = (int) (((float) o.reference.health / (float) o.reference.maxHealth) * 4);
					switch (o.reference.injury) {
					case crumples:
						rCompSub = (p == 3 ? "dented " : p == 2 ? "bent " : p == 1 ? "crumpled-up " : "crushed ")
								+ o.reference.compSub;
						break;
					case shatters:
						rCompSub = (p == 3 ? "fractured " : p > 0 ? "cracked " : "shattered ")
								+ o.reference.compSub;
						break;
					case squishes:
						rCompSub = (p == 3 ? "bruised "
								: p == 2 ? "squashed " : p == 1 ? "compressed " : "trampled ")
								+ o.reference.compSub;
						break;
					case bruises:
						rCompSub = (p == 3 ? "bruised "
								: p == 2 ? "damaged " : p == 1 ? "beaten-up " : "pulverized ")
								+ o.reference.compSub;
						break;
					}
				}
			}
			
			
			if (protag.hunger > 0) {
				if (rand.nextInt(101 - protag.hunger) < 5 && o.reference != null) {
					compSub = lRandOf(new String[] { "possibly edible", "juicy and tender", "appetizing",
							"delicious-looking", "scrumptious" }) + " " + compSub;
				}
			}

			int n = i + 1;
			String s = null;
			while (s == null) {
				try {
					s = protag.currentRoom.objects.get(n).reference.accessor;
					if (s.equals(o.reference.accessor)) {
						x1 = 2;
					}
					break;
				} catch (NullPointerException e) {

				} catch (IndexOutOfBoundsException e) {
					break;
				}
				n++;
			}
			n = i - 1;
			s = null;
			while (s == null) {
				try {
					s = protag.currentRoom.objects.get(n).reference.accessor;
					if (s.equals(o.reference.accessor)) {
						x2 = 1;
					}
					break;
				} catch (NullPointerException e) {

				} catch (IndexOutOfBoundsException e) {
					break;
				}
				n--;
			}

			try {
				Object r = o.reference;
				if (r != null) {
					Terminal.print("(1000)");
					if (x1 == 1) {
						if (x2 == 0) {
							Terminal.print(lRandOf(
									new String[] { " as well as a " + compSub + " " + o.description + " " + rCompSub,
											" and a " + compSub + " " + o.description + " " + rCompSub }));
						} else {
							Terminal.print(", and a " + compSub + " " + o.description + " " + rCompSub);
						}
					} else if (x1 == 2) {
						if (x2 == 0) {
							Terminal.print(
									uRandOf(new String[] { "there is a " + compSub, "You notice a " + compSub }));
						} else {
							Terminal.print(", a " + compSub);
						}
					} else {
						Terminal.print(
								uRandOf(new String[] { "there is a " + compSub + " " + o.description + " " + rCompSub,
										o.description + " " + rCompSub + ", there is a " + compSub,
										"You notice a " + compSub + " " + o.description + " " + rCompSub }));
					}
					if (x1 > 0) {
						x1--;
					}
					if (x1 == 0) {
						x2 = 0;
						Terminal.println(".");
					}
				}
			} catch (NullPointerException e) {

			}
		}
	}
	
	public void runObjects() {
		objectQueue.clear();
		for (Object o : protag.currentRoom.objects) {
			if (o.health != null && o.health <= 0) {
				for (Object obj : o.container) {
					obj.reference = protag.currentRoom.floor;
					obj.description = lRandOf(new String[] { "lying", "sitting", "resting" }) + " on";

				}
				o.container.clear();
			}
		}
		
		for (Object o : protag.inventory) {
			if (o.health != null && o.health <= 0) {
				for (Object obj : o.container) {
					obj.reference = protag.currentRoom.floor;
					obj.description = lRandOf(new String[] { "lying", "sitting", "resting" }) + " on";

				}
				o.container.clear();
			}
		}
		
		Iterator<Object> objectIt = protag.currentRoom.objects.iterator();
		while (objectIt.hasNext()) {
			Object o = objectIt.next();
			if(!o.abstractObj) {
			if (o.alive && o.health <= 0) {
				if (o.getClass().getSimpleName().equals("Entity")) {
					Entity e = (Entity) o;
					int s = objectQueue.size();
					e.death.accept(this);
					for (Object obj : e.inventory) {
						if (objectQueue.size() != s) {
							objectQueue.get(s).container.addAll(e.inventory);
						} else {
							obj.reference = protag.currentRoom.floor;
							obj.description = "on";
							objectQueue.add(obj);
						}
					}
					objectIt.remove();
				}
			} else if (!o.alive && o.health <= 0) {
				for (Object obj : o.container) {
					objectQueue.add(obj);
				}
				o.container.clear();
			}
			}
		}
		for (Object o : protag.currentRoom.objects) {
			try {
				Entity e = (Entity) o;
				e.interactable = e.check(protag);
			} catch (Exception e) {
			}
		}
		protag.currentRoom.objects.addAll(objectQueue);
	}

<<<<<<< HEAD
			Terminal.println(protag.health > 90 ? ""
					: protag.health > 50 ? "You are feeling slightly injured."
							: protag.health > 0
									? "You think that you might have some injuries, but you've forgotten where."
									: "You are dead.");
=======
	public void update() {
		String userText;
>>>>>>> master

		
		
		runObjects();
		
		
		Terminal.println(protag.health > 90 ? ""
				: protag.health > 50 ? "You are feeling slightly injured."
						: protag.health > 0
								? "You think that you might have some injuries, but you've forgotten where."
								: "You feel slightly dead, but you aren't sure.");
		
		outerloop:
		while (true) {// repeats until valid command
			Terminal.print("(1000)");
			if (protag.currentRoom != null) {
				if (changedSurroundings) {
			inspectRoom();
			changedSurroundings = false;
				} else {
				Room holder = protag.currentRoom;
				String desc = holder.description;
				while (holder.fatherRoom != null) {
					holder = holder.fatherRoom;
					desc = holder.description + ": " + desc;
				}

				Terminal.println(desc);
				}
			} else
				Terminal.println("Currently not in any room!");

			userText = Terminal.readln();
			userText = userText.toLowerCase();
			
			for (String str : omitWords) {
				userText = userText.replace(" " + str + " ", " ");
			}
			
			String[] parts = userText.split("with");
			
			String[] prepUsed = new String[parts.length];
			
			for (int i = 0; i < parts.length; i++) {//probably some of the ugliest code (by me) in my life, but it works
				prepUsed[i] = "";
				parts[i] = parts[i].trim();
				int w = 0;
				while (true) {
					for (String str: prepositions) {
						if (parts[i].substring(w, parts[i].substring(w).indexOf(' ') == -1? parts[i].length(): parts[i].substring(w).indexOf(' ') + w).equals(str)) {
							prepUsed[i] += " " + str;
							parts[i] = parts[i].substring(0, w).trim() + " " + parts[i].substring(parts[i].substring(w).indexOf(' ') == -1? parts[i].length(): parts[i].substring(w).indexOf(' ') + w).trim();
							//System.out.println("1: " + parts[i]);
							break;
						}
					}
					
					if (parts[i].substring(w).indexOf(' ') == -1)
						break;
					
					w = parts[i].substring(w).indexOf(' ') + 1;
				}
			}
			
			//words = new ArrayList<String>();

			ArrayList<String> words = new ArrayList<String>();
			words.addAll(Arrays.asList(parts[0].split(" ")));
			if (words.size() > 2) {
				Terminal.println("What do you mean?");
				continue;
			}
			
			for (int i = 1; i < parts.length; i++)
				words.addAll(Arrays.asList(parts[i].trim().split(" ")));
			
			//System.out.println(words);
			
			/*for (String str : s) {
				if (!str.isEmpty()) {
					words.add(str);// user text goes to array of words
				}
			}*/

			/*if (words.size() != 2) {
				Terminal.println("All commands must be 2 words.");
				continue;
			}*/

			Word w0 = null;
			Word w1 = null;
			Object o1 = null;
			Object o2 = null;
			boolean found = false;
			boolean foundObject = false;

			for (Word w : vocabulary) {
				if (w.checkWord(words.get(0))) {
					w0 = w;
					found = true;
				}
			} // finds word in array

			if (!found) {
				Terminal.println("I don't know what '" + words.get(0) + "' means.");
				continue;
			}

			if (w0.getClass() != Verb.class) {
				Terminal.println("Commands always start with a verb.");
				continue;
			}

			found = false;

			for (Word w : vocabulary) {
				if (w.checkWord(words.get(1))) {
					try {
						if (w.represents == null) {
							boolean b = (Boolean) null;
						}
						o1 = (Object) w.represents;
						foundObject = true;
					} catch (Exception e) {
						w1 = w;
						found = true;
					}
				}
			}

			for (Object o : protag.currentRoom.objects) {
				if (o.accessor.equals(words.get(1))) {
					o1 = o;
					foundObject = true;
				}
				for (Object obj : o.container) {
					if (obj.accessor.equals(words.get(1))) {
						o1 = obj;
						foundObject = true;
					}
				}
			}

			for (Object o : protag.inventory) {
				if (o.accessor.equals(words.get(1))) {
					o1 = o;
					foundObject = true;
				}
				for (Object obj : o.container) {
					if (obj.accessor.equals(words.get(1))) {
						o1 = obj;
						foundObject = true;
					}
				}
				if (words.size() > 2 && o.accessor.equals(words.get(2))) {
					o2 = o;
				}
			}
			try {
			if (protag.rightHand.accessor.equals(words.get(1))) {
				o1 = protag.rightHand;
				foundObject = true;
			}
			if (words.size() > 2 && protag.rightHand.accessor.equals(words.get(2))) {
				o2 = protag.rightHand;
			}
			} catch(NullPointerException e) {}

			if (!found && !foundObject) {
				String str = (" " + protag.currentRoom.description.replace(".", " ").replace(",", " ").replace(";", " ")
						.replace(":", " ") + " ").toLowerCase();
				if (str.contains(" " + words.get(1) + " ")) {

					Terminal.println("No.");
					continue;
				}

				for (Object o : protag.currentRoom.objects) {
					try {
						if (o.reference.abstractObj && o.reference.accessor.equalsIgnoreCase(words.get(1))) {
							Terminal.println("No.");
							continue outerloop;
						}
					} catch (Exception e) {
					}
				}

				Terminal.println("I don't know what '" + words.get(1) + "' means.");
				continue;
			}

			if (found && !foundObject) {
				if (w1.getClass() == Verb.class) {
					Terminal.println("Commands never end with a verb.");
					continue;
				}
			}

			if (found) {
				try {
					w0.perform(w1, prepUsed[0], this);// fills out word's function
				} catch(Exception exc) {
					Terminal.println(uRandOf(new String[] {"I'm not sure what you want me to do.", "Who, me?", "Error. Please try again.", "This isn't going to work out."}));
					continue;
				}
			} else if (foundObject) {
				if (o2 == null)
					w0.perform(o1, prepUsed[0], this);
				else {
					w0.perform(o1, o2, prepUsed[0], prepUsed[1], this);
				}
			}

			updatePlayerState();
			Iterator<Effect> effectIt = protag.effects.iterator();
			while (effectIt.hasNext()) {
				Effect e = effectIt.next();
				e.affect(protag);
				if (e.lifetime == 0) {
					effectIt.remove();
				}
			}
			break;
		}
	}
}
