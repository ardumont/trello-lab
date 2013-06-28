# trello-lab

A Clojure library designed to ... well, that part is up to you.

## Usage

1. open an org-mode file
2. edit the identity of the desired board (must have been presetup with the at least 3 columns - todo, doing, done)
3. create a todo list following this line:

```org-mode
* card-identity
** checklist
*** task1
*** task2
*** task3
```

Trello:
- `card-identity` will be created in trello in the `todo` column.
- `checklist` will be added to the card `card-identity`
- the 3 tasks are added to the checklist `checklist`

It is to be noted that no move has taken place yet.

4. User has done the task 1 and fill his/her org-mode:

```org-mode
* card-identity
** checklist
*** DONE task1
*** task2
*** task3
```

Trello:
- The card `card-identity` is moved to the `doing` column.
- The task task1 is checked.

5. User has finished all tasks
```org-mode
* card-identity
** checklist
*** DONE task1
*** DONE task2
*** DONE task3
```

Trello:
- The card `card-identity` is moved to the `done` column.
- The tasks `task1`, `task2`, `task3` are checked.

6. User add other tasks
```org-mode
* card-identity
** checklist
*** DONE task1
*** DONE task2
*** DONE task3
*** task4
```

Trello:
- the card `card-identity` is moved back to the `doing` column.

7. User pass all tasks to todo
```org-mode
* card-identity
** checklist
*** task1
*** task2
*** task3
*** task4
```

Trello:
- the card `card-identity` is moved back to todo.

## License

Copyright © 2013 commiters

Distributed under the Eclipse Public License, the same as Clojure.
