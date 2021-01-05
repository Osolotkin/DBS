public class Main {

    private static final Database database = new Database();
    private static final Menu mainMenu = new Menu();

    public static void main(String[] args) {

        if (Config.load() == 1) {
            Utils.writeError("Could not load config from file!");
        }

        if (Config.ALIGNMENT == Database.Align.LEFT) {
            System.out.println("LEFT");
        } else if (Config.ALIGNMENT == Database.Align.RIGHT) {
            System.out.println("RIGHT");
        } else if (Config.ALIGNMENT == Database.Align.CENTER) {
            System.out.println("CENTER");
        }
        // connecting to the database
        if (database.connect() == 1) {
            Utils.writeError("Could not connect to the data base");
            return;
        } else {
            if (Config.VERBOSE) {
                Utils.writeInfo("Database connection was successful");
            }
        }

        /*

        main menu structure or something like that

            - select table
                - show table
                - insert row
                - delete row
                    - delete
                    - show rows
                    - return
                - find
                - return

            - change pet owner

            - print all pets treatments, just info

            - print all pets procedures with all doctors

            - print receipt

            - how many treatments has each pet (SELECT inside SELECT)

            - print how many different drugs are used in each procedure in specific interval (SELECT inside FROM)

            - print procedures that have price above average or lower (SELECT inside WHERE)

            - print hwo many pets have each pet owner (usage of GROUP BY)

            - print all doctors that hasn't participated in any treatment case (set operation)

            - print doctors names that have healing the pet (LEFT JOIN)

            - exit


         */

        MExecution selectTable = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equals(HELP)) {
                    writeHelp(
                            "Selecting table specified by name.",
                            new String[] {
                                    "name", "name of the table, names has to be spaceless."
                            }
                    );
                    return;
                }

                if (!database.existTable(args[0])) {
                    Utils.writeError("Cannot find table: " + args[0]);
                    return;
                }

                final String selectedTable = args[0];
                System.out.println();
                System.out.println("Selected table: " + selectedTable);

                Menu subMenu = new Menu();

                MExecution showTable = new MExecution() {
                    public void execute(String args[]) {

                        boolean isNull = args == null;

                        if (!isNull && args[0].equals("help")) {
                            writeHelp(
                                    "Displaying top rows of selected table.",
                                    new String[] {
                                            "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                            "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                                    }
                            );
                            return;
                        }

                        database.renderTable(
                                selectedTable,
                                (!isNull && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                                (!isNull && args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                        );

                    }
                };
                subMenu.addItem("Show table", showTable);

                MExecution FindRows = new MExecution() {
                    public void execute(String[] args) {

                        if (nullCheck(args)) return;

                        final String[][] colInfo = database.getColNamesAndTypes(selectedTable);

                        if (args[0].equalsIgnoreCase(HELP)) {
                            writeHelp(
                                    "Finds rows by given arguments." + Utils.NEW_LINE + "Write in such format: " + Utils.strarr2str(colInfo[0]) + Utils.NEW_LINE + "To skip not needed cols use " + Config.SKIP_CHAR,
                                    null
                            );
                            return;
                        }

                        args = Utils.fillTheLength(args, colInfo[0].length,Character.toString(Config.SKIP_CHAR));

                        String[][] argsToSearch = Utils.pickNotEmptyPairs(new String[][] { colInfo[0], args, colInfo[1] }, Config.SKIP_CHAR);

                        String[][] rows = database.getRows(selectedTable, argsToSearch);
                        database.renderRows(rows, colInfo[0], (args.length > colInfo[0].length) ? Utils.str2int(args[colInfo[0].length]) : Config.DEFAULT_COLUMN_WIDTH);

                    }
                };
                subMenu.addItem("Find rows", FindRows);

                MExecution insertRow = new MExecution() {
                    public void execute(String[] args) {

                        if (nullCheck(args)) return;

                        if (args[0].equalsIgnoreCase(HELP)) {
                            final String[] colNames = database.getColNames(selectedTable);
                            writeHelp(
                                    "Inserts row into selected table." + Utils.NEW_LINE + "Write in such format: " + Utils.strarr2str(colNames) + Utils.NEW_LINE + "To skip not needed cols use " + Config.SKIP_CHAR,
                                    null
                            );
                            return;
                        }

                        final String[][] colInfo = database.getColNamesAndTypes(selectedTable);

                        args = Utils.fillTheLength(args, colInfo[0].length,Character.toString(Config.SKIP_CHAR));

                        String[][] tmp = Utils.pickNotEmptyPairs(new String[][] { colInfo[0],  args, colInfo[1] }, Config.SKIP_CHAR);
                        if (database.insertRow(selectedTable, tmp) == 1) {
                            Utils.writeError("Could not insert row!");
                            return;
                        }

                        Utils.writeInfo("Row was successfully inserted!");

                    }
                };
                subMenu.addItem("Insert row", insertRow);

                MExecution deleteRow = new MExecution() {
                    public void execute(String[] args) {

                        if (nullCheck(args)) return;

                        if (args[0].equalsIgnoreCase(HELP)) {
                            final String[] colNames = database.getColNames(selectedTable);
                            writeHelp(
                                    "Deletes rows from selected table." + Utils.NEW_LINE + "Write in such format: " + Utils.strarr2str(colNames) + Utils.NEW_LINE + "To skip not needed cols use " + Config.SKIP_CHAR,
                                    null
                            );
                            return;
                        }

                        final String[][] colInfo = database.getColNamesAndTypes(selectedTable);

                        args = Utils.fillTheLength(args, colInfo[0].length, Character.toString(Config.SKIP_CHAR));

                        String[][] tmp = Utils.pickNotEmptyPairs(new String[][] { colInfo[0], args, colInfo[1] }, Config.SKIP_CHAR);
                        String[][] rows;
                        if ((rows = database.getRows(selectedTable, tmp)) == null) {
                            Utils.writeError("Could not delete row!");
                            return;
                        }

                        Menu deleteMenu = new Menu();
                        System.out.println("Do you really want to delete " + ((rows.length > 1) ? rows.length + " rows" : "row") + "?");
                        MExecution delete = new MExecution() {
                            public void execute(String[] args) {
                                if (database.deleteRows(selectedTable, tmp) != 0) {
                                    Utils.writeError("Could not delete row!");
                                    return;
                                }
                                Utils.writeInfo(((rows.length > 1) ? rows.length + " rows" : "row") + " was successfully deleted!");
                            }
                        };
                        deleteMenu.addItem("Delete", delete);

                        MExecution show = new MExecution() {
                            public void execute(String[] args) {
                                database.renderRows(rows, colInfo[0], (args != null && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_COLUMN_WIDTH);
                            }
                        };
                        deleteMenu.addItem("Show rows", show);

                        MExecution goBack = new MExecution() {
                            public void execute(String[] args) {
                            }
                        };
                        deleteMenu.addItem("Return", goBack);

                        while (true) {
                            deleteMenu.render();
                            int choice = deleteMenu.checkForInput();
                            if (choice == 3 || choice == 1) return;
                        }

                    }
                };
                subMenu.addItem("Delete row", deleteRow);

                MExecution returnBack = new MExecution() {
                    public void execute(String[] args) {
                        return;
                    }
                };
                subMenu.addItem("Return", returnBack);

                while (true) {
                    subMenu.render();
                    if (subMenu.checkForInput() == 5) return;
                }

            }
        };
        mainMenu.addItem("Select table", selectTable);

        MExecution changePetOwner = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Changes pet owner.",
                            new String[] {
                                    "pet id", "necessary, integer value, if not converted to zero, determines id of the pet you want to process action",
                                    "new pet owner id", "necessary, integer value, if not converted to zero, determines new owner of the selected pet"
                            }
                    );
                    return;
                }

                if (args.length < 2) {
                    Utils.writeError("Not enough number of params." + Utils.NEW_LINE + "This function has to have two params: pet_id and owner_id!");
                    return;
                }
                database.changePetOwner(Utils.str2int(args[0]), Utils.str2int(args[1]));

            }
        };
        mainMenu.addItem("Change Pet Owner", changePetOwner);

        MExecution printTreatments = new MExecution() {
            public void execute(String[] args) {

                boolean isNull = args == null;

                if (!isNull && args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints treatment cases",
                            new String[] {
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printTreatmentCases(
                        (!isNull && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                        (!isNull && args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("Print treatment cases", printTreatments);

        MExecution printProceduresInfo = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints procedures info of the selected pet by id.",
                            new String[] {
                                    "pet id", "necessary, integer value, if not converted to zero, determines id of the pet which procedures info will be shown.",
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printProceuresInfo(
                        Utils.str2int(args[0]),
                        (args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_TOP_ROWS,
                        (args.length > 2) ? Utils.str2int(args[2]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("Print procedures info", printProceduresInfo);

        MExecution printReceipt = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints receipt for specific pet owner.",
                            new String[] {
                                    "pet owner id", "necessary, integer value, if not converted to zero, determines id of the pet owner."
                            }
                    );
                    return;
                }

                database.printReceipt(Utils.str2int(args[0]));

            }
        };
        mainMenu.addItem("Print receipt", printReceipt);




        MExecution SELECTinsideSELECT = new MExecution() {
            public void execute(String[] args) {

                boolean isNull = args == null;

                if (!isNull && args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints how many treatments has each pet.",
                            new String[] {
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }


                database.printPetTreatmentCount(
                        (!isNull && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                        (!isNull && args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(SELECT inside SELECT)", SELECTinsideSELECT);

        MExecution SELECTinsideFROM = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints drug count of each procedure on specific interval.",
                            new String[] {
                                    "bottom bound", "necessary, integer value, if not converted to zero, specifies bottom bound of the interval.",
                                    "upper bound", "unnecessary, integer value, if not converted to zero, specifies upper bound of the interval.",
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printProceduresDrugCountInt(
                        (args.length > 0) ? Utils.str2int(args[0]) : 0,
                        (args.length > 1) ? Utils.str2int(args[1]) : Integer.MAX_VALUE,
                        (args.length > 2) ? Utils.str2int(args[2]) : Config.DEFAULT_TOP_ROWS,
                        (args.length > 3) ? Utils.str2int(args[3]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(SELECT inside FROM)", SELECTinsideFROM);

        MExecution SELECTinsideWHERE = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints procedures that have price above average or below.",
                            new String[] {
                                    "aver side", "necessary, 1 for above, something other for below",
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printProceduresAverPrice(
                        args[0].equalsIgnoreCase("1"),
                        (args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_TOP_ROWS,
                        (args.length > 2) ? Utils.str2int(args[2]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(SELECT inside WHERE)", SELECTinsideWHERE);

        MExecution GROUPBY = new MExecution() {
            public void execute(String[] args) {

                boolean isNull = args == null;

                if (!isNull && args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints hwo many pets have each pet owner.",
                            new String[] {
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printPetOwnersPetCount(
                        (!isNull && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                        (!isNull && args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(GROUP BY)", GROUPBY);

        MExecution setOperation = new MExecution() {
            public void execute(String[] args) {

                boolean isNull = args == null;

                if (!isNull && args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints all doctors that hasn't participated in any treatment case.",
                            new String[] {
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printNoActionDoctors(
                        (!isNull && args.length > 0) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                        (!isNull && args.length > 1) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(set operation)", setOperation);

        MExecution LEFTJOIN = new MExecution() {
            public void execute(String[] args) {

                if (nullCheck(args)) return;

                if (args[0].equalsIgnoreCase(HELP)) {
                    writeHelp(
                            "Prints doctors names that have healing the pet",
                            new String[] {
                                    "pet id", "necessary, integer value, if not converted to zero, determines id of the pet.",
                                    "top rows", "unnecessary, integer value, if not converted to zero, specifies number of rows that will be rendered, if given number is higher than number of rows in selected table, draws em all, default value: " + Config.DEFAULT_TOP_ROWS,
                                    "col width", "unnecessary, integer value, if not converted to zero, specifies width of each column in chars, min width is 3, some fancy spaces and other stuff, that has nothing to do with table value, but decoration, does not count, default value: " + Config.DEFAULT_COLUMN_WIDTH
                            }
                    );
                    return;
                }

                database.printDoctorsForThePet(
                        Utils.str2int(args[0]),
                        (args.length > 1) ? Utils.str2int(args[0]) : Config.DEFAULT_TOP_ROWS,
                        (args.length > 2) ? Utils.str2int(args[1]) : Config.DEFAULT_COLUMN_WIDTH
                );

            }
        };
        mainMenu.addItem("(LEFT JOIN)", LEFTJOIN);

        MExecution exit = new MExecution() {
            public void execute(String[] args) {
            }
        };
        mainMenu.addItem("Exit", exit);

        while(true) {
            mainMenu.render();
            if (mainMenu.checkForInput() == 12) break;
        }

    }

}
