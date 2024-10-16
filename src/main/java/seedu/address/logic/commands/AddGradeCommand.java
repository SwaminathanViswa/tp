package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE;

import java.util.Objects;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Add assignment grades to an existing person in the address book.
 */
public class AddGradeCommand extends Command {
    public static final String COMMAND_WORD = "addGrade";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD
                    + ": Adds a grade of an assignment to the person. "
                    + "Parameters: "
                    + PREFIX_NAME
                    + "NAME "
                    + PREFIX_ASSIGNMENT
                    + "ASSIGNMENT "
                    + PREFIX_SCORE
                    + "SCORE "
                    + "Example: "
                    + COMMAND_WORD
                    + " "
                    + PREFIX_NAME
                    + "John Doe "
                    + PREFIX_ASSIGNMENT
                    + "Ex09 "
                    + PREFIX_SCORE
                    + "9 ";

    private final Name personName;
    private final Float score;
    private final String assignmentName;

    /**
     * @param personName     Name of the person.
     * @param score          Score of the assignment.
     * @param assignmentName Name of assignment.
     */
    public AddGradeCommand(String personName, Float score, String assignmentName) {
        requireAllNonNull(personName, score, assignmentName);
        this.personName = new Name(personName);
        this.score = score;
        this.assignmentName = assignmentName;
    }

    private static Person createGradeToAddToPerson(Person person, String assignmentName, float score) {
        assert person != null;
        Name name = person.getName();
        Phone phone = person.getPhone();
        Email email = person.getEmail();
        Address address = person.getAddress();
        Set<Tag> tags = person.getTags();
        Telegram telegram = person.getTelegram();
        Github github = person.getGithub();
        Assignment assignment =
                new Assignment(assignmentName, score);

        return new Person(name, phone, email, address, telegram, tags, github, assignment);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        // check if assignment is in predefined list
        if (!model.hasAssignment(assignmentName)) {
            throw new CommandException("Invalid assignment name: " + assignmentName);
        }

        if (score > model.maxScore(assignmentName) || score < 0) {
            throw new CommandException("Score must be between 0.0 and " + model.maxScore(assignmentName));
        }

        if (!model.hasName(personName)) {
            throw new CommandException("Person " + personName + " not in address book");
        }

        Person person =
                model.getAddressBook().getPersonList().stream()
                        .filter(p -> p.getName().equalIgnoreCase(personName))
                        .toList()
                        .get(0);
        model.setPerson(person, createGradeToAddToPerson(person, model.getAssignmentName(assignmentName), score));
        return new CommandResult(""); // placeholder string to be added
    }

    @Override
    public String toString() {
        return personName + " " + assignmentName + " " + score;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AddGradeCommand otherCommand) {
            return otherCommand.personName.equals(personName)
                    && Objects.equals(otherCommand.assignmentName, assignmentName)
                    && Objects.equals(otherCommand.score, score);

        }
        return false;
    }
}
