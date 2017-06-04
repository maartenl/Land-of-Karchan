export class Family {
  toname: string;
  description: string;
  public static readonly FAMILYVALUES = [
    "unknown",
    "father",
    "mother",
    "son",
    "daughter",
    "husband",
    "wife",
    "friend",
    "sister",
    "brother",
    "cousin",
    "grandfather",
    "grandmother",
    "uncle",
    "aunt",
    "nephew",
    "niece",
    "follower",
    "teacher"
  ];

  constructor() {
  }

  public getDescriptionAsInteger(): string {    
    for (let v in Family.FAMILYVALUES) {
      if (this.description === Family.FAMILYVALUES[v]) {
        return v;
      }
    }
    throw "Could not translate familyrelation.";
  }

}
