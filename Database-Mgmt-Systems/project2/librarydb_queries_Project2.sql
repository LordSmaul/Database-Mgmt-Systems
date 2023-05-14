-- 1. For each library member his card number, first, middle and last name along with the number of book copies he ever borrowed. There may be members who didn't ever borrow any book copy.
-- ------------------------------
select card_no, first_name, middle_name, last_name, count(distinct barcode) as barcode_count 
from member natural join borrow
group by card_no;
-- ________________________________
-- 2. Members (their card numbers, first, middle and last names) who held a book copy the longest.
-- There can be one such member or more than one.
-- Don't take into account a case that someone borrowed the same book copy again.
-- Don't take into account members who borrowed a book copy and didn't return it yet.
-- --------------------------------
select card_no, first_name, middle_name, last_name
from member natural join borrow
where date_returned is not null
group by card_no
having max(datediff(date_returned, date_borrowed));
-- ________________________________
-- 3. For each book (ISBN and title) the number of copies the library owns.
-- --------------------------------
select ISBN, title, count(*) as copy_number
from book natural join copy 
group by ISBN
order by count(*) desc;
-- ________________________________
-- 4. Books (ISBNs and titles), if any, having exactly 3 authors.
-- --------------------------------
select ISBN, title
from book natural join book_author
group by ISBN
having count(*) = 3;
-- ________________________________
-- 5. For each author (ID, first, middle and last name) the number of books he wrote.
-- --------------------------------
select author.author_id, first_name, middle_name, last_name, count(*) as book_count
from author natural join book_author
group by author_id
order by author_id asc;
-- ________________________________
-- 6. Card number, first, middle and last name of members, if any, who borrowed some book by Chartrand(s). 
-- Remove duplicates from the result.
-- --------------------------------
select distinct card_no, member.first_name, member.middle_name, member.last_name
from member natural join borrow natural join copy natural join book_author, author
where book_author.author_id = author.author_id
and author.last_name = "Chartrand";
-- ________________________________
-- 7. Most popular author(s) (their IDs and first, middle and last names) in the library.
-- --------------------------------
select author_id, first_name, middle_name, last_name
from borrow natural join copy natural join book_author natural join author
group by author_id
order by count(*) desc limit 3;
-- ________________________________
-- 8. Card numbers, first, middle, last names and addresses of members whose libray card will expire within the next month.
-- --------------------------------
select card_no, first_name, middle_name, last_name, 
concat('#', apt_no, '  ', street, ' St., ', city, ', ', state, ' ', zip_code) as address
from member
where datediff(card_exp_date, curdate()) <= 31;
-- ________________________________
-- 9. Card numbers, first, middle and last names of members along with the amount of money they owe to the library. 
-- Assume that if a book copy is returned one day after the due date, a member owes 0.25 cents to the library.
-- --------------------------------
select card_no, first_name, middle_name, last_name,
sum(datediff(date_returned, date_borrowed) * 0.25) as money_owed
from member natural join borrow
where date_returned is not null
and (renewals_no = 0 and datediff(date_returned, date_borrowed) > 14)
or (renewals_no = 1 and datediff(date_returned, date_borrowed) > 28)
or (renewals_no = 2 and datediff(date_returned, date_borrowed) > 42)
group by card_no;
-- ________________________________
-- 10. The amount of money the library earned (received money for) from late returns.
-- --------------------------------
select sum(datediff(date_returned, date_borrowed) * 0.25) as money_owed
from borrow
where date_returned is not null
and (renewals_no = 0 and datediff(date_returned, date_borrowed) > 14)
or (renewals_no = 1 and datediff(date_returned, date_borrowed) > 28)
or (renewals_no = 2 and datediff(date_returned, date_borrowed) > 42)
group by card_no;
-- ________________________________
-- 11. Members (their card numbers, first, middle and last names) who borrowed more non-fiction books than fiction books.
-- --------------------------------
select card_no, first_name, middle_name, last_name
from member natural join borrow natural join copy natural join book natural join genre
group by card_no;
-- ________________________________
-- 12. Name of the most popular publisher(s).
-- --------------------------------
select publisher, count(*) as publisher_count
from borrow natural join copy natural join book
group by publisher
order by publisher_count desc;
-- ________________________________
-- 13. Members (card numbers, first, middle and last names) who never borrowed any book copy and whose card expired.
-- --------------------------------
select card_no
from member
where member.card_no not in (select card_no from borrow)
and left(card_exp_date,2) < month(curdate()) and right(card_exp_date,2) <= right(year(curdate()),2);
-- ________________________________
-- 14. The most popular genre(s).
-- --------------------------------
select genre_id, name, count(*) as borrow_count 
from borrow natural join copy natural join book natural join genre
group by genre_id;
-- ________________________________
-- 15. For each state, in which some member lives, the most popular last name(s). 
-- --------------------------------
select last_name, state, count(*) as last_name_count
from member
group by last_name, state
order by count(*) desc;
-- ________________________________
-- 16. Books (ISBNs and titles) that don't have any authors. 
-- --------------------------------
select ISBN, title
from book natural join book_author
group by ISBN
having count(*) = 0;
-- ________________________________
-- 17. Members (card numbers) who borrowed the same book more than once (not necessarily the same copy of a book).
-- --------------------------------
select distinct card_no
from borrow natural join copy natural join book
group by card_no, ISBN
having count(*) > 1;
-- ________________________________
-- 18. Number of members from Cookeville, TN and from Algood, TN.
-- --------------------------------
select phone_no
from member
where city = "Cookeville" or city = "Algood";
-- ________________________________
-- 19. Card numbers and emails of members who should return a book copy tomorrow. If these members didn't renew their loan twice, then they still have a chance to renew their loan. If they won't renew or return a book tomorrow, then they will be charged for the following day(s).
-- --------------------------------
select member.card_no, email_address
from borrow natural join member
where date_returned is null
and ((datediff(date_add(date_borrowed, interval 2 week), date_borrowed) = 1 and renewals_no = 0)
or (datediff(date_add(date_borrowed, interval 4 week), date_borrowed) = 1 and renewals_no = 1)
or (datediff(date_add(date_borrowed, interval 6 week), date_borrowed) = 1 and renewals_no = 2));
-- ________________________________
-- 20. Condition of a book copy that was borrowed the most often, not necessarily held the longest.
-- --------------------------------
select comment
from borrow natural join copy
group by comment, ISBN
order by count(*) desc
limit 1;
-- ________________________________