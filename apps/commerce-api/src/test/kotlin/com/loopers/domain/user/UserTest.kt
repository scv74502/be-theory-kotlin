package com.loopers.domain.user

import com.loopers.domain.user.UserSteps.Companion.기본_생년월일
import com.loopers.domain.user.UserSteps.Companion.기본_인코더
import com.loopers.domain.user.UserSteps.Companion.기본_이름
import com.loopers.domain.user.UserSteps.Companion.기본_로그인_ID
import com.loopers.domain.user.UserSteps.Companion.기본_비밀번호
import com.loopers.domain.user.UserSteps.Companion.기본_이메일
import com.loopers.domain.user.UserSteps.Companion.비밀번호_생성
import com.loopers.domain.user.UserSteps.Companion.회원_도메인_생성
import com.loopers.domain.user.vo.Password
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UserTest {
    @Test
    fun `모든_필드가_유효하면_회원이_정상_생성된다`() {
        val user = 회원_도메인_생성()

        assertThat(user.loginId).isEqualTo(기본_로그인_ID)
        assertThat(user.name).isEqualTo(기본_이름)
        assertThat(user.birthday).isEqualTo(기본_생년월일)
        assertThat(user.email).isEqualTo(기본_이메일)
        assertThat(user.password.encoded).isEqualTo("ENC($기본_비밀번호)")
    }

    @Test
    fun `로그인ID에_영숫자_외_문자가_있으면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(loginId = "한글ID!") }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `로그인ID가_4자_미만이면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(loginId = "abc") }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `로그인ID가_20자_초과면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(loginId = "a".repeat(21)) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `이름이_공백이면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(name = "   ") }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `이메일_형식이_잘못되면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(email = "not-an-email") }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `생년월일이_미래_일자면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { 회원_도메인_생성(birthday = LocalDate.now().plusDays(1)) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호가_8자_미만이면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("Ab1!abc", 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호가_16자_초과면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("Ab1!" + "a".repeat(14), 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_대문자가_없으면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("password1!", 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_소문자가_없으면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("PASSWORD1!", 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_숫자가_없으면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("Password!!", 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_특수문자가_없으면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> { Password.of("Password11", 기본_생년월일, 기본_인코더()) }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_yyyyMMdd_생년월일_토큰이_포함되면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> {
            비밀번호_생성(rawPassword = "Pw19900514!", birthday = LocalDate.of(1990, 5, 14))
        }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_yyMMdd_생년월일_토큰이_포함되면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> {
            비밀번호_생성(rawPassword = "Pw900514A!", birthday = LocalDate.of(1990, 5, 14))
        }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }

    @Test
    fun `비밀번호에_MMdd_생년월일_토큰이_포함되면_BAD_REQUEST가_발생한다`() {
        val ex = assertThrows<CoreException> {
            비밀번호_생성(rawPassword = "Pw0514AB!", birthday = LocalDate.of(1990, 5, 14))
        }
        assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
    }
}
