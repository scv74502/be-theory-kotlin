package com.loopers.domain.user

import com.loopers.domain.user.exception.InvalidPasswordException
import com.loopers.domain.user.exception.InvalidUserException
import com.loopers.domain.user.port.PasswordEncoder
import com.loopers.domain.user.vo.Birthday
import com.loopers.domain.user.vo.Email
import com.loopers.domain.user.vo.LoginId
import com.loopers.domain.user.vo.Name
import com.loopers.domain.user.vo.Password
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UserVoValidationTest {
    @Test
    fun `로그인ID는_4자와_20자_경계값이면_생성된다`() {
        assertThat(LoginId.of("a123").value).isEqualTo("a123")
        assertThat(LoginId.of("a".repeat(20)).value).isEqualTo("a".repeat(20))
    }

    @Test
    fun `로그인ID는_4자_미만이거나_20자_초과면_생성이_불가하다`() {
        assertThrows<InvalidUserException> { LoginId.of("a12") }
        assertThrows<InvalidUserException> { LoginId.of("a".repeat(21)) }
    }

    @Test
    fun `로그인ID는_영숫자가_아니면_생성이_불가하다`() {
        assertThrows<InvalidUserException> { LoginId.of("abc!") }
        assertThrows<InvalidUserException> { LoginId.of("abc ") }
        assertThrows<InvalidUserException> { LoginId.of("한글123") }
    }

    @Test
    fun `이름은_1자와_50자_경계값이면_생성된다`() {
        assertThat(Name.of("김").value).isEqualTo("김")
        assertThat(Name.of("가".repeat(50)).value).isEqualTo("가".repeat(50))
    }

    @Test
    fun `이름은_공백이거나_50자_초과면_생성이_불가하다`() {
        assertThrows<InvalidUserException> { Name.of("") }
        assertThrows<InvalidUserException> { Name.of("   ") }
        assertThrows<InvalidUserException> { Name.of("가".repeat(51)) }
    }

    @Test
    fun `생년월일은_과거_일자면_생성된다`() {
        val yesterday = LocalDate.now().minusDays(1)

        assertThat(Birthday.of(yesterday).value).isEqualTo(yesterday)
    }

    @Test
    fun `생년월일은_오늘이거나_미래_일자면_생성이_불가하다`() {
        assertThrows<InvalidUserException> { Birthday.of(LocalDate.now()) }
        assertThrows<InvalidUserException> { Birthday.of(LocalDate.now().plusDays(1)) }
    }

    @Test
    fun `이메일은_간이_RFC_5322_형식이면_생성된다`() {
        assertThat(Email.of("user@example.com").value).isEqualTo("user@example.com")
        assertThat(Email.of("user.name+tag@example.co.kr").value).isEqualTo("user.name+tag@example.co.kr")
    }

    @Test
    fun `이메일은_골뱅이나_도메인_TLD가_없으면_생성이_불가하다`() {
        assertThrows<InvalidUserException> { Email.of("user.example.com") }
        assertThrows<InvalidUserException> { Email.of("user@") }
        assertThrows<InvalidUserException> { Email.of("user@example") }
        assertThrows<InvalidUserException> { Email.of("user@example.c") }
    }

    @Test
    fun `비밀번호는_8자와_16자_경계값이면_생성된다`() {
        assertThat(Password.of("Abcde1!@", birthday(), encoder()).encoded).isEqualTo("ENC(Abcde1!@)")
        assertThat(Password.of("AbcdefghijklmN1!", birthday(), encoder()).encoded).isEqualTo("ENC(AbcdefghijklmN1!)")
    }

    @Test
    fun `비밀번호는_8자_미만이거나_16자_초과면_생성이_불가하다`() {
        assertThrows<InvalidPasswordException> { Password.of("Abcd1!@", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("AbcdefghijklmNo1!", birthday(), encoder()) }
    }

    @Test
    fun `비밀번호는_필수_문자종류가_없으면_생성이_불가하다`() {
        assertThrows<InvalidPasswordException> { Password.of("abcdef1!", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("ABCDEF1!", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Abcdefg!", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Abcdef12", birthday(), encoder()) }
    }

    @Test
    fun `비밀번호는_허용되지_않은_문자가_있으면_생성이_불가하다`() {
        assertThrows<InvalidPasswordException> { Password.of("Abc한글1!", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Abcde1! ", birthday(), encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Abcde1!~", birthday(), encoder()) }
    }

    @Test
    fun `비밀번호는_생년월일_토큰을_포함하면_생성이_불가하다`() {
        val birthday = Birthday.of(LocalDate.of(1990, 5, 14))

        assertThrows<InvalidPasswordException> { Password.of("Pw19900514!", birthday, encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Pw900514A!", birthday, encoder()) }
        assertThrows<InvalidPasswordException> { Password.of("Pw0514AB!", birthday, encoder()) }
    }

    private fun birthday(): Birthday = Birthday.of(LocalDate.of(1990, 5, 14))

    private fun encoder(): PasswordEncoder = mockk {
        every { encode(any()) } answers { "ENC(" + firstArg<String>() + ")" }
    }
}
