// This is a simple number guessing game that represents some of LScript's syntax.

int hidden = randint(1, 10);

bool found = false;
int guesses = 0;
while (!found) {
    num guess = numinput("Guess a number! ");
    if (typeof(guess) != 'int') {
        print("Please input an integer between 1 and 10.");
        continue;
    }
    guesses++;
    if (hidden == guess) {
        found = true;
    } else {
        print("Try again!");
    }
}
print("You found the number in a total of %guesses guesses!");