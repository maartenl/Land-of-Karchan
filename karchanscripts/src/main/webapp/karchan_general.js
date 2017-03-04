// $Id:$

/**
 * The Global Karchan Object. Accessible from *anywhere in the world*.
 * Isn't it amazing? (Exaggeration!)
 * We can use this object to create a namespace for my karchan methods
 * functions and constants.
 */
var Karchan = Karchan || {
  deputy_email: "deputiesofkarchan@outlook.com",
  getGenericError: function() {
    return "An error occurred. Please notify Karn or one of the deps at " + this.deputy_email + ".";
  }
};
