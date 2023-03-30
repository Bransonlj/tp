package seedu.internship.logic.commands;

import seedu.internship.commons.util.CollectionUtil;
import seedu.internship.logic.commands.exceptions.CommandException;
import seedu.internship.logic.parser.CliSyntax;
import seedu.internship.model.Model;
import seedu.internship.model.internship.Company;
import seedu.internship.model.internship.Internship;
import seedu.internship.model.internship.Position;
import seedu.internship.model.internship.Status;
import seedu.internship.model.tag.Tag;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Finds internships from the catalogue based on predicates provided by the user.
 */
public class FindCommand extends Command {
    public static final String COMMAND_WORD = "find";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds internships from the catalogue based on predicates provided by the user.\n"
            + "Parameters: [" + CliSyntax.PREFIX_POSITION + "POSITION] "
            + "[" + CliSyntax.PREFIX_COMPANY + "COMPANY]"
            + "[" + CliSyntax.PREFIX_STATUS + "STATUS]\n"
            + "Example: " + COMMAND_WORD + " p/ Software Engineer" + " c/ Grab";

    public static final String MESSAGE_SUCCESS = "Found internships : %1$s";

    public static final String MESSAGE_NOT_FILTERED = "At least one field to filter must be provided.";


    private final FilterInternshipDescriptor filterInternshipDescriptor;

    public FindCommand(FilterInternshipDescriptor filterInternshipDescriptor) {
        requireNonNull(filterInternshipDescriptor);
        this.filterInternshipDescriptor = filterInternshipDescriptor;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Internship> lastShownList = model.getFilteredInternshipList();
        Predicate<Internship> filterPos = unused -> true;
        Predicate<Internship> filterCom = unused -> true;
        Predicate<Internship> filterStat = unused -> true;
        Predicate<Internship> filterTags = unused -> true;
        if (filterInternshipDescriptor.getPosition().isPresent()) {
            filterPos = x -> x.getPosition().positionName.toLowerCase().contains(filterInternshipDescriptor.getPosition()
                    .get().positionName.toLowerCase());
        }
        if (filterInternshipDescriptor.getCompany().isPresent()) {
            filterCom = x -> x.getCompany().companyName.toLowerCase().contains(filterInternshipDescriptor.getCompany()
                    .get().companyName.toLowerCase());
        }
        if (filterInternshipDescriptor.getStatus().isPresent()) {
            filterStat = x -> x.getStatus().equals(filterInternshipDescriptor.getStatus().get());
        }

        if (filterInternshipDescriptor.getTags().isPresent()) {
            filterStat = x -> x.getTags().containsAll(filterInternshipDescriptor.getTags().get());
        }
        Predicate<Internship> finalFilterPos = filterPos;
        Predicate<Internship> finalFilterCom = filterCom;
        Predicate<Internship> finalFilterStat = filterStat;
        Predicate<Internship> finalFilterTags = filterTags;
        Predicate<Internship> filter = x -> finalFilterPos.test(x) && finalFilterCom.test(x) && finalFilterStat.test(x)
                && finalFilterTags.test(x);
        model.updateFilteredInternshipList(filter);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredInternshipList().size()),
                ResultType.FIND);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        // state check
        FindCommand f = (FindCommand) other;
        return filterInternshipDescriptor.equals(f.filterInternshipDescriptor);
    }


    public static class FilterInternshipDescriptor {
        private Position position;
        private Company company;
        private Status status;
        private Set<Tag> tags;

        public FilterInternshipDescriptor() {
        }

        /**
         * Returns true if at least one field is filtered.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(position, company, status, tags);
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Optional<Position> getPosition() {
            return Optional.ofNullable(position);
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public Optional<Company> getCompany() {
            return Optional.ofNullable(company);
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public Optional<Status> getStatus() {
            return Optional.ofNullable(status);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof FilterInternshipDescriptor)) {
                return false;
            }

            // state check
            FilterInternshipDescriptor f = (FilterInternshipDescriptor) other;

            return getPosition().equals(f.getPosition())
                    && getCompany().equals(f.getCompany())
                    && getStatus().equals(f.getStatus());
        }
    }
}
