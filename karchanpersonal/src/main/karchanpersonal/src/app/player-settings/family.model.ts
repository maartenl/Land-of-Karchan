export class Family {
  public static readonly FAMILYVALUES = [
    'unknown',
    'father',
    'mother',
    'son',
    'daughter',
    'husband',
    'wife',
    'friend',
    'sister',
    'brother',
    'cousin',
    'grandfather',
    'grandmother',
    'uncle',
    'aunt',
    'nephew',
    'niece',
    'follower',
    'teacher'
  ];

  toname: string;

  description: string;

  constructor() {
    this.toname = '';
    this.description = '';

  }

}
