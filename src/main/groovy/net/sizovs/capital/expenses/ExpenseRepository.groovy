package net.sizovs.capital.expenses

import net.sizovs.capital.FirebaseRepository
import org.springframework.stereotype.Repository

@Repository
class ExpenseRepository extends FirebaseRepository {

    def expensesRef = root.child("expenses")

    void save(Expense expense) {
        expensesRef.push().value = expense
    }

}
