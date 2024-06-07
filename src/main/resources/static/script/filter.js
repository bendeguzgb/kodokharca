const PARAMETER_SEPARATOR = "&";
const FILTER_VALUE_SEPARATOR = "-";
const FILTER_SEPARATOR = ",";
const ELEMENTS_IN_ARRAY = "dbszam=";
const FILTER_STRING = "szuresek=";
const SEARCH = window.location.search;


window.addEventListener("DOMContentLoaded", function() {
    // set selected array size in radio button
    let countOfElements = SEARCH.match(`(&|\\?)?${ELEMENTS_IN_ARRAY}\\d`)[0]
    countOfElements = countOfElements.substring(countOfElements.length - 1);
    document.getElementById(`elements${countOfElements}`).checked = true;

    // override 'onsubmit' of the selector of number of player's numbers
    const elementsInArrayForm = document.getElementById("elementsInArrayForm");

    elementsInArrayForm.onsubmit = function () {
        for (let input of elementsInArrayForm.getElementsByTagName("input")) {
            if (input.type !== "radio") {
                continue;
            }

            if (input.checked) {
                let number = input.id.replace("elements", "");
                let newElementsInArrayParameter = `${ELEMENTS_IN_ARRAY}${number}`;

                window.location.search = SEARCH.replace(new RegExp(`${ELEMENTS_IN_ARRAY}\\d`), newElementsInArrayParameter);
                return false;
            }
        }
        return false;
    }


    // override 'onsubmit' functions of filters
    for (let form of document.getElementsByClassName('filter')) {
        form.onsubmit = function() {
            const id = this.id;

            let filterIndex;
            let currentFilters;
            let parameters = SEARCH.split(PARAMETER_SEPARATOR);

            for (let i = 0; i < parameters.length; i++) {
                if (parameters[i].includes(FILTER_STRING)) {
                    currentFilters = parameters[i];
                    filterIndex = i;
                    break;
                }
            }

            if (currentFilters === undefined) {
                parameters.push(FILTER_STRING); //separator not needed bc of join
                currentFilters = FILTER_STRING;
                filterIndex = parameters.length - 1;
            }

            if (currentFilters.includes(id)) {
                alert(`Hiba! \nEz a szűrés már megtörtént: '${id}'\nSzűrések: '${currentFilters.replace(FILTER_STRING, "")}'`);
                return false;
            }

            let filter = (currentFilters.endsWith("=")) ? id : (FILTER_SEPARATOR + id);
            let isRadioChecked = false;

            for (let input of this.getElementsByTagName("input")) {
                if (input.type === "submit" || isRadioChecked) {
                    continue;
                }

                filter += FILTER_VALUE_SEPARATOR;

                if (input.type === "radio") {
                    let isOn = input.checked;
                    let isFound = ["páros", "fekete", "igen"].includes(input.labels[0].innerText.toLowerCase());

                    filter += (isFound && isOn || !isFound && !isOn);
                    isRadioChecked = true;
                } else {
                    filter += input.value;
                }
            }

            parameters[filterIndex] = currentFilters + filter;
            window.location.search = parameters.join(PARAMETER_SEPARATOR);

            return false;
        };
    }
});
